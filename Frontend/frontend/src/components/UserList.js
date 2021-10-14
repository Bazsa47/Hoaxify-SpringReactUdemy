import React from 'react';
import * as apiCalls from '../api/apiCalls';
import UserListItem from './UserListItem'

class UserList extends React.Component{

    state = {
        page:{
            content:[],
            number: 0,
            size : 3
        }
    }

    componentDidMount(){
        this.loadData();
    }

    onClickNext = () => {
        this.loadData(this.state.page.number+1);
    }

    onClickPrevious = () => {
        this.loadData(this.state.page.number-1)
    }

    loadData = (requestedPage = 0) => {
        apiCalls.listUsers({page: requestedPage, size: this.state.page.size})
        .then((response)=>{
            this.setState({
                page: response.data,
                loadError: undefined
            })
        }).catch(()=>{
            this.setState({loadError: 'User load failed'})
        });
    }
    render(){
        return(
            <div className="card">
                <h3 className="card-title m-auto">Users</h3>
                <div className="list-group list-group-flush" data-testid="usergroup">
                    {
                        this.state.page.content.map(user => {
                            return (
                                <UserListItem key={user.username} user={user}/>
                            );
                        })
                    }
                </div>
                <div className="clearfix">
                    {!this.state.page.first && <span style={{cursor:"pointer", float:"left"}} onClick={this.onClickPrevious}>{`< Previous`}</span>}
                    {!this.state.page.last && <span style={{cursor:"pointer", float:"right"}} onClick={this.onClickNext}>Next ></span>}
                </div>
                {this.state.loadError && <span className ="text-center text-danger">{this.state.loadError}</span>}
            </div>
        );
    }
}

export default UserList;