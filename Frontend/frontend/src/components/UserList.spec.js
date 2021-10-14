import React from 'react';
import {render, fireEvent,waitForElement} from '@testing-library/react';
import UserList from './UserList';
import * as apiCalls from '../api/apiCalls';
import {MemoryRouter} from 'react-router-dom';

apiCalls.listUsers = jest.fn().mockResolvedValue({
    data:{
        content:[],
        number:0,
        size:3
    }
})

const setup = () => {
    return render(<MemoryRouter><UserList/></MemoryRouter>)
}

const mockedEmptySuccesResponse = {
    data:{
        content:[],
        number:0,
        size:3
    }
}

const mockedSuccesGetSinglePage = {
    data:{
        content:[
            {
                username:'user1',
                displayName:'display1',
                image:''
            },
            {
                username:'user2',
                displayName:'display2',
                image:''
            },
            {
                username:'user3',
                displayName:'display3',
                image:''
            }
        ],
        number:0,
        first: true,
        last:true,
        size:3,
        totalPages:1
    }
}

const mockedSuccesGetMultiPageFirst = {
    data:{
        content:[
            {
                username:'user1',
                displayName:'display1',
                image:''
            },
            {
                username:'user2',
                displayName:'display2',
                image:''
            },
            {
                username:'user3',
                displayName:'display3',
                image:''
            }
        ],
        number:0,
        first: true,
        last:false,
        size:3,
        totalPages:2
    }
}

const mockedSuccesGetMultiPageLast = {
    data:{
        content:[
            {
                username:'user4',
                displayName:'display4',
                image:''
            }
        ],
        number:1,
        first: false,
        last:true,
        size:3,
        totalPages:2
    }
}

const mockFailGet = {
    response:{
        data:{
            message:'Load error'
        }
    }
}
describe('UserList',()=>{
    describe('Layout',()=>{
        it('has header of users',()=>{
            const {container} = setup();
            const header = container.querySelector('h3');
            expect(header).toHaveTextContent('Users');
        })

        /*it('displays three items when listuser api returns three users',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetSinglePage);
            const {queryByTestId} = setup();
            await waitForDomChange();
            const userGroup = queryByTestId('usergroup');
            expect(userGroup.childElementCount).toBe(3);
        })
        it('displays displayname@username when uer api returns user',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetSinglePage);
            const {queryByText} = setup();
            const firstUser = await waitForElement(()=> queryByText('display1@user1'));
            expect(firstUser).toBeInTheDocument();
        }) 
        it('displays the next button when response has last value as false',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetMultiPageFirst);
            const {queryByText} = setup();
            const nextLink = await waitForElement(()=> queryByText('Next >'));
            expect(nextLink).toBeInTheDocument();
        }) 
        it('hides the next button when response has last value as true',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetMultiPageLast);
            const {queryByText} = setup();
            const nextLink = await waitForElement(()=> queryByText('Next >'));
            expect(nextLink).not.toBeInTheDocument();
        }) 
        it('displays the previous button when response has first value as false',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetMultiPageLast);
            const {queryByText} = setup();
            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            expect(prevLink).toBeInTheDocument();
        })
        it('hides the previous button when response has first value as true',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetMultiPageFirst);
            const {queryByText} = setup();
            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            expect(prevLink).not.toBeInTheDocument();
        }) 
        it('has link to user page',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedSuccesGetSinglePage);
            const {queryByText, container} = setup();
            await waitForElement(()=> queryByText('display1@user1'));

            const firstAnchor = container.querySelectorAll('a')[0];
            expect(firstAnchor.getAttribute('href')).toBe('/user1');
        }) */
    })
    describe('Lifecycle',()=>{
        it('calls listUsers api when its rendered',()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedEmptySuccesResponse);
            setup();

            expect(apiCalls.listUsers).toHaveBeenCalledTimes(1);
        })

        it('calls listUsers method with page=0 and size=3',()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValue(mockedEmptySuccesResponse);
            setup();

            expect(apiCalls.listUsers).toHaveBeenCalledWith({page:0,size:3});
        })
    })
    describe('Interactions',()=>{
       /* it('loads next page when clicked next button',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValueOnce(mockedSuccesGetMultiPageFirst).mockResolvedValueOnce(mockedSuccesGetMultiPageLast);
            const {queryByText} = setup();
            const nextLink = await waitForElement(()=> queryByText('Next >'));
            fireEvent.click(nextLink);

            const secondPageUser  =await waitForElement(()=> queryByText('display4@user4'));
            expect(secondPageUser).toBeInTheDocument();
        }) 
        it('loads previous page when clicked previous button',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValueOnce(mockedSuccesGetMultiPageLast).mockResolvedValueOnce(mockedSuccesGetMultiPageFirst);
            const {queryByText} = setup();
            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            fireEvent.click(prevLink);

            const firstPageUser = await waitForElement(()=> queryByText('display1@user1'));
            expect(firstPageUser).toBeInTheDocument();
        }) 
        it('displays error msg when loading userpage fails',async ()=>{
            apiCalls.listUsers = jest.fn().mockResolvedValueOnce(mockedSuccesGetMultiPageLast).mockRejectedValueOnce(mockFailGet);
            const {queryByText} = setup();
            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            fireEvent.click(prevLink);

            const errorMsg = await waitForElement(()=> queryByText('User load failed'));
            expect(errorMsg).toBeInTheDocument();
        }) 
        it('hides error msg when successfully loading other page',async ()=>{
            apiCalls.listUsers = jest.fn()
            .mockResolvedValueOnce(mockedSuccesGetMultiPageLast)
            .mockRejectedValueOnce(mockFailGet)
            .mockResolvedValueOnce(mockedSuccesGetMultiPageFirst);
            const {queryByText} = setup();
            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            fireEvent.click(prevLink);

            const prevLink = await waitForElement(()=> queryByText('< Previous'));
            fireEvent.click(prevLink);

            const errorMsg = await waitForElement(()=> queryByText('User load failed'));
            expect(errorMsg).not.toBeInTheDocument();
        })*/
    })
})

console.error = () => {};