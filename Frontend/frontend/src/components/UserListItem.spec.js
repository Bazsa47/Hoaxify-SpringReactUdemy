import React from 'react';
import {render} from '@testing-library/react';
import UserListItem from './UserListItem';
import {MemoryRouter} from 'react-router-dom';

const user={
    username: 'user1',
    displayName: 'display1',
    image: 'profile1.png'
};

const setup = (propUser = user) => {
    return render(
        <MemoryRouter><UserListItem user= {propUser}/></MemoryRouter>
    );
}

describe('UserListItem',()=>{
    it('has image',()=>{
        const {container} = setup();
        const img = container.querySelector('img');
        expect(img).toBeInTheDocument();
    })

    it('displays default img when user has none',()=>{
        const userWithoutImage ={
            ...user,
            image: undefined
        }
        const {container} = setup(userWithoutImage)
        const img = container.querySelector('img');
        expect(img.src).toContain('/profile.png');
    })

    it('displays user img when user has one',()=>{

        const {container} = setup()
        const img = container.querySelector('img');
        expect(img.src).toContain('/images/profile/'+user.image);
    })


})