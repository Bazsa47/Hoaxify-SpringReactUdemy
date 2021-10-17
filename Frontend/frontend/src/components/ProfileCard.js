import React from 'react';
import ProfileImageWithDefault from '../components/ProfileImageWithDefault';
import Input from "../components/Input";
import ButtonWithProgress from '../components/ButtonWithProgress';

const ProfileCard = (props) => {
    const {displayName,username, image} = props.user;

    const showEditButton = props.isEditable && !props.editMode
    return (
        <div className = "card">
            <div className ="card-header text-center">
                <ProfileImageWithDefault 
                image = {image} 
                alt="profile" 
                width="200" 
                height ="200"
                className ="rounded-circle shadow"
                
                />
            </div>
            <div className="card-body text-center">
               {!props.editMode && <h4>{`${displayName}@${username}`}</h4>}
                {props.editMode && <div className="mb-2">
                                        <Input 
                                            value={displayName}
                                            label={`Change Display Name for ${username}`}
                                            onChange= {props.onChangeDisplayName}
                                        />
                                  </div>}
                {showEditButton && <button className="btn btn-outline-success" onClick={props.onClickEdit}>
                    <i className="fas fa-user-edit"/>Edit
                </button>}
                {
                    props.editMode && (
                        <div>
                        <ButtonWithProgress 
                            className="btn btn-primary" 
                            onClick={props.onClickSave} 
                            text={<span><i className="fas fa-save"/>Save</span>}
                            pendingApiCall={props.pendingUpdateCall}
                            disabled ={props.pendingUpdateCall}
                        />
                            
                        <button className="btn btn-outline-secondary m-1" onClick={props.onClickCancel} disabled ={props.pendingUpdateCall}>
                            <i className="fas fa-window-close"/>Cancel
                        </button>
                        </div>

                    )
                }
                
            </div>
        </div>
    );
};

export default ProfileCard;