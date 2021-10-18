import React from 'react';
import {render} from '@testing-library/react';
import ProfileCard from './ProfileCard';

const user = {
    id: 1,
    username: 'user1',
    displayName: 'display1',
    image: 'profile1.png'
}


describe('ProfileCard',()=>{
    describe('Layout',()=>{
        it('Displays the displayname@username',()=>{
            const {queryByText} = render(<ProfileCard user={user}/>)
            const userInfo = queryByText('display1@user1');
            expect(userInfo).toBeInTheDocument();
        })
        it('Displays image',()=>{
            const {container} = render(<ProfileCard user={user}/>)
            const img = container.querySelector("img");
            expect(img).toBeInTheDocument();
        })
        it('Displays default image when user does not have one',()=>{
            const userWithouImage = {
                ...user,
                image: undefined
            }
            const {container} = render(<ProfileCard user={userWithouImage}/>)
            const img = container.querySelector("img");
            expect(img.src).toContain("/profile.png");
        })
        it('Displays user image when user has one',()=>{
            const {container} = render(<ProfileCard user={user}/>)
            const img = container.querySelector("img");
            expect(img.src).toContain("/images/profile/"+user.image);
        })

        it('Displays edit button when editable property is set to true',()=>{
            const {queryByText} = render(<ProfileCard user={user} isEditable={true}/>)
            const editButton = queryByText('Edit');
            expect(editButton).toBeInTheDocument();
        })

        it('does not Display edit button when editable property is not provided',()=>{
            const {queryByText} = render(<ProfileCard user={user}/>)
            const editButton = queryByText('Edit');
            expect(editButton).not.toBeInTheDocument();
        })

        it('does displayname input when editMode property is true',()=>{
            const {container} = render(<ProfileCard user={user} editMode={true}/>)
            const displayNameInput = container.querySelector('Input');
            expect(displayNameInput).toBeInTheDocument();
        })
        it('does displayname input when editMode property is true',()=>{
            const {container} = render(<ProfileCard user={user} editMode={true}/>)
            const displayNameInput = container.querySelector('Input');
            expect(displayNameInput.value).toBe(user.displayName);
        })
        it('Disables the displayname@username in edit mode',()=>{
            const {queryByText} = render(<ProfileCard user={user} editMode={true}/>)
            const userInfo = queryByText('display1@user1');
            expect(userInfo).not.toBeInTheDocument();
        })
        it('Displays label for input in edit mode',()=>{
            const {container} = render(<ProfileCard user={user} editMode={true}/>)
            const label = container.querySelector('label');
            expect(label).toHaveTextContent('Change Display Name for user1');
        })
        it('Hides the edit button in edit mode and isEditable provided as true',()=>{
            const {queryByText} = render(<ProfileCard user={user} editMode={true} isEditable={true}/>)
            const saveButton = queryByText('Save');
            expect(saveButton).toBeInTheDocument();
        })
        it('Displays save button in edit mode',()=>{
            const {queryByText} = render(<ProfileCard user={user} editMode={true} isEditable={true}/>)
            const editButton = queryByText('Edit');
            expect(editButton).not.toBeInTheDocument();
        })
        it('Displays cancel button in edit mode',()=>{
            const {queryByText} = render(<ProfileCard user={user} editMode={true} isEditable={true}/>)
            const cancelButton = queryByText('Cancel');
            expect(cancelButton).toBeInTheDocument();
        })
        it('Displays  file input it edit mode',()=>{
            const {container} = render(<ProfileCard user={user} editMode={true} isEditable={true}/>)
            const inputs = container.querySelectorAll('input');
            const uploadInput = inputs[1];
            expect(uploadInput.type).toBe('file');
        })

    })
})