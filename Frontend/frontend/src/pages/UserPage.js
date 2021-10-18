import React from 'react';
import * as apiCalls from '../api/apiCalls';
import {connect} from 'react-redux';
import ProfileCard from '../components/ProfileCard';

class UserPage extends React.Component{

    state = {
        user: undefined,
        userNotFound : false,
        isLoading:false,
        editMode: false,
        originalDisplayname: undefined,
        pendingUpdateCall: false,
        image: undefined,
        errors: {}
    }

    onClickEdit = () =>{
        this.setState({
            editMode:true
        });
    }

    onClickCancel = () =>{
        const user = {...this.state.user};
        if(this.state.originalDisplayname !== undefined){
            user.displayName = this.state.originalDisplayname;
        }
        this.setState({
            user,
            originalDisplayname : undefined,
            editMode:false,
            image:undefined,
            errors: {}
        });
    }

    onFileSelect = (event) => {
        if(event.target.files.length ===0 ) return
        const errors = {...this.state.error}
        errors.image = undefined;
        const file = event.target.files[0];
        let reader = new FileReader();
        reader.onloadend = () =>{
            this.setState({image:reader.result, errors})
        }
        reader.readAsDataURL(file);
    }

    onClickSave = () =>{
        const userId = this.props.loggedInUser.id;
        const userUpdate = {
            displayName: this.state.user.displayName,
            image: this.state.image && this.state.image.split(',')[1]
        }
        this.setState({pendingUpdateCall:true});
        apiCalls.updateUser(userId,userUpdate).then(response=>{
            const user = {...this.state.user}
            user.image = response.data.image;
            this.setState({editMode:false, originalDisplayname:undefined, pendingUpdateCall:false, user, image:undefined})
        }).catch(error => {
            let errors = {};
            if(error.response.data.validationErrors){
                
                errors = error.response.data.validationErrors;
            }
            this.setState({pendingUpdateCall:false, errors});
        });
    }

    onChangeDisplayName = (event) => {
        const user = {...this.state.user};
        let originalDisplayname = this.state.originalDisplayname;
        if(originalDisplayname === undefined){
            originalDisplayname = user.displayName;
        }
        user.displayName = event.target.value;
        const errors = {...this.state.errors}
        errors.displayName= undefined
        this.setState({user, originalDisplayname,errors});
    }

    loadUser = () => {
        const username = this.props.match.params.username;
        if(!username) return;
        this.setState({userNotFound: false, isLoading:true})
        apiCalls.getUser(username).then((response)=>{
            this.setState({user: response.data, isLoading:false})
        }).catch((error)=>{
            this.setState({userNotFound : true, isLoading:false})
        });
    }

    componentDidMount(){
        this.loadUser();
    }

    componentDidUpdate(prevProps){
        if(prevProps.match.params.username !== this.props.match.params.username){
            this.loadUser();
        }
    }

    render(){

        let pageContent;
        if(this.state.isLoading){
            pageContent = (
                <div className="d-flex">
                    <div className="spinner-border m-auto text-black-50"></div>
                </div>

            );
        }
        else if(this.state.userNotFound){
            pageContent = ( 
                <div className="alert alert-danger text-center">
                    <div className ="alert-heading">
                        <i className ="fas fa-exclamation-triangle fa-3x"></i>
                    </div>
                    <h5>
                        User not found
                    </h5>
                </div>
            );
        }else{
            const isEditable = this.props.loggedInUser.username === this.props.match.params.username;
            pageContent = this.state.user && <ProfileCard 
                                                user={this.state.user} 
                                                isEditable={isEditable} 
                                                editMode={this.state.editMode}
                                                onClickEdit = {this.onClickEdit} 
                                                onClickCancel={this.onClickCancel}
                                                onClickSave = {this.onClickSave}
                                                onChangeDisplayName = {this.onChangeDisplayName}
                                                pendingUpdateCall ={this.state.pendingUpdateCall}
                                                loadedImage ={this.state.image}
                                                onFileSelect = {this.onFileSelect}
                                                errors={this.state.errors}
                                                />
        }
        return(
            <div data-testid="userpage">{pageContent}</div>
        );
    }
}

UserPage.defaultProps ={
    match:{
        params:{}
    }
}

const mapStateToProps = (state) => {
    return{
        loggedInUser : state
    }
}

export default connect(mapStateToProps)(UserPage);