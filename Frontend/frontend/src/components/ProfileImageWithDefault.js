import React from 'react';
import defaultPicture from '../assets/profile.png';

const ProfileImageWithDefault = (props) => {
    let imageSrc = defaultPicture;
    if(props.image) imageSrc=`/images/profile/${props.image}`;
    return (
        <img 
        {...props} 
        src={props.src || imageSrc} 
        onError = {event => { event.target.src = defaultPicture;}}
        />
    );
};

export default ProfileImageWithDefault;