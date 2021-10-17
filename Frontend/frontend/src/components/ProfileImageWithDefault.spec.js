import React from 'react';
import {render, fireEvent} from '@testing-library/react';
import ProfileImageWithDefault from './ProfileImageWithDefault';

describe('ProfileImageWithDefault',()=>{
    describe('Layout',()=>{
        it('has image',()=>{
            const {container} = render(<ProfileImageWithDefault/>)
            const img = container.querySelector('img');
            expect(img).toBeInTheDocument();
        })
        it('displays default image when image property is not provided',()=>{
            const {container} = render(<ProfileImageWithDefault/>)
            const img = container.querySelector('img');
            expect(img.src).toContain("/profile.png");
        })
        it('displays user image when image property is provided',()=>{
            const {container} = render(<ProfileImageWithDefault image="profile1.png"/>)
            const img = container.querySelector('img');
            expect(img.src).toContain("/images/profile/profile1.png");
        })
        it('displays default image when provided image loading fails',()=>{
            const {container} = render(<ProfileImageWithDefault image="profile1.png"/>)
            const img = container.querySelector('img');
            fireEvent.error(img);
            expect(img.src).toContain("/profile.png");
        })
        it('displays the image provided through src property',()=>{
            const {container} = render(<ProfileImageWithDefault src="image-from-src-png"/>)
            const img = container.querySelector('img');

            expect(img.src).toContain("/image-from-src-png");
        })
    })
})