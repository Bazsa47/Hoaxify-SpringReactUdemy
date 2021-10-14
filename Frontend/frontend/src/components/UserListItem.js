import React from 'react';
import defaultPicture from '../assets/profile.png';
import {Link} from 'react-router-dom';

const UserListItem = props =>{

    let imageSrc = defaultPicture;
    if(props.user.image) {
        imageSrc =`/images/profile/${props.user.image}`
    }
    return(
        <Link to={`/${props.user.username}`} className="list-group-item list-group-item-action">
            <img className="rounded-circle" src={imageSrc} alt="profile" width="32" height ="32"/>
            <span className="pl-20">
            {`${props.user.displayName}@${props.user.username}`}
            </span>
        </Link>
    );
}

export default UserListItem;