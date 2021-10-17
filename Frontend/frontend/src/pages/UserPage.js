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
        pendingUpdateCall: false
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
            editMode:false
        });
    }

    onClickSave = () =>{
        const userId = this.props.loggedInUser.id;
        const userUpdate = {
            displayName: this.state.user.displayName
        }
        this.setState({pendingUpdateCall:true});
        apiCalls.updateUser(userId,userUpdate).then(response=>{
            this.setState({editMode:false, originalDisplayname:undefined, pendingUpdateCall:false})
        }).catch(error => {
            this.setState({pendingUpdateCall:false});
        });
    }

    onChangeDisplayName = (event) => {
        const user = {...this.state.user};
        let originalDisplayname = this.state.originalDisplayname;
        if(originalDisplayname === undefined){
            originalDisplayname = user.displayName;
        }
        user.displayName = event.target.value;
        this.setState({user, originalDisplayname});
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