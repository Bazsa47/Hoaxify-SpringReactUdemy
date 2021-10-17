import React from 'react';
import ProfileImageWithDefault from '../components/ProfileImageWithDefault';
import {Link} from 'react-router-dom';

const UserListItem = props =>{

    return(
        <Link to={`/${props.user.username}`} className="list-group-item list-group-item-action">
            <ProfileImageWithDefault 
            className="rounded-circle" 
            image = {props.user.image}
            alt="profile" 
            width="32" 
            height ="32"
            />
            <span className="pl-20">
            {`${props.user.displayName}@${props.user.username}`}
            </span>
        </Link>
    );
}

export default UserListItem;