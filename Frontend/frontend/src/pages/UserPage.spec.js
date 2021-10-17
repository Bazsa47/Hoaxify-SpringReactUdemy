import React from 'react';
import {render,waitForElement,fireEvent, WaitForDomChange} from '@testing-library/react';
import UserPage from './UserPage';
import * as apiCalls from '../api/apiCalls';
import {Provider} from 'react-redux';
import configureStore from '../redux/configureStore';
import axios from "axios";

const mockSuccesGetUser = {
    data:{
        id:1,
        username:'user1',
        displayName:'display1',
        image:'profile1.png'
    }
}

const mockSuccesUpdateUser = {
    data:{
        id:1,
        username:'user1',
        displayName:'display1-update',
        image:'profile1-update.png'
    }
}

const mockFailGetUser = {
    respone:{
        data:{
            message: 'User not found'
        }
    }
}

const mockFailUpdateUser = {
    response:{
        data:{

        }
    }
}

const match = {
    params:{
        username:'user1'
    }
}

const setup = (props) => {
    const store = configureStore(false);
    return render(
        <Provider store={store}>
            <UserPage {...props}/>
        </Provider>

    );
}

beforeEach(()=>{
    localStorage.clear();
    delete axios.defaults.headers.common['Authorization']
})

const setUserOneLoggedInStorage = ()=> {
    localStorage.setItem('hoax-auth',JSON.stringify({
        id:1,
        username:'user1',
        displayName:'display1',
        image:'profile1.png',
        password:'P4ssword',
        isLoggedIn: true
    }))
  }

describe('UserPage',() =>{
    describe('Layout',()=>{
        it('has root page div',() => {
            const {queryByTestId} = setup();
            const userPageDiv = queryByTestId('userpage');
            expect(userPageDiv).toBeInTheDocument();
        })
        /*it('displays the displayName@username when user data loaded', async ()=>{
            apiCalls.getUser = jest.fn().mockResolvedValue(mockSuccesGetUser);
            const {queryByText} = setup({match});
            const text = await waitForElement(()=> queryByText('display1@user1'));
            expect(text).toBeInTheDocument();
        })
        it('displays not found alert when user not found', async ()=>{
            apiCalls.getUser = jest.fn().mockRejectValue(mockFailGetUser);
            const {queryByText} = setup({match});
            const alert = await waitForElement(()=> queryByText('User not found'));
            expect(alert).toBeInTheDocument();
        })
        it('displays spinner while loading user data',  ()=>{
            const mockDelayedResponse = jest.fn().mockImplementation(()=>{
                return new Promise((resolve,reject) => {
                    setTimeout(()=>{
                        resolve(mockSuccesGetUser)
                    },300);
                })
            });
            apiCalls.getUser = jest.fn().mockDelayedResponse;
            const {queryByText} = setup({match});
            const spinner = queryByText('Loading...');
            expect(spinner).toBeInTheDocument();
        }) 
        it('displays the edit button when LoggedInUser matches the user in url', async ()=>{
            setUserOneLoggedInStorage();
            apiCalls.getUser = jest.fn().mockResolvedValue(mockSuccesGetUser);
            const {queryByText} = setup({match});
            await waitForElement(()=> queryByText('display1@user1'));
            const editButton = queryByText("Edit");
            expect(editButton).toBeInTheDocument();
        }) */
        const setupForEdit = async () => {
            setUserOneLoggedInStorage();
                apiCalls.getUser = jest.fn().mockResolvedValue(mockSuccesGetUser);
                const {rendered} = setup({match});
                await waitForElement(()=> rendered.queryByText('Edit'));
                const editButton = queryByText("Edit");
                fireEvent.click(editButton);
                return rendered;
        }

        const mockDelayedUpdateSuccess = () =>{
            jest.fn().mockImplementation(()=>{
                return new Promise((resolve,reject)=>{
                    setTimeout(()=>{
                        resolve(mockSuccesUpdateUser)
                    },300)
                })
            });
        }
        /*describe('Profile Card Layout',()=>{
            it('displays edit layout when clicking edit button', async ()=>{
                
                const {queryByText} = await setupForEdit();
                expect(queryByText('Save')).toBeInTheDocument();
            })

            it('returns back to non edit mode when clicking cancel', async ()=>{
                const {queryByText} = await setupForEdit();

                const cancelButton = queryByText("Cancel");
                fireEvent.click(cancelButton);

                expect(queryByText('Edit')).toBeInTheDocument();
            }) 
            it('calls update user api when clicking save', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockSuccesUpdateUser);
                const save = queryByText("Save");
                fireEvent.click(save);

                expect(apiCalls.updateUser).toBeCalledTimed(1);
            })

            it('calls update user api with user id', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockSuccesUpdateUser);
                const save = queryByText("Save");
                fireEvent.click(save);

                const userId = apiCalls.updateUser.mock.calls[0][0];
                expect(userId).toBe(1);
            })

            it('calls update user api with request body having changed diplayname', async ()=>{
                const {queryByText, container} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockSuccesUpdateUser);

                const input = container.querySelector('input');
                fireEvent.change(input,{target:{value:'display1-update'}})
                const save = queryByText("Save");
                fireEvent.click(save);

                const requestBody = apiCalls.updateUser.mock.calls[0][1];
                expect(requestBody.displayName).toBe('display1-update');
            }) 

            it('returns to non edit mode after successfull save', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockSuccesUpdateUser);
                const save = queryByText("Save");
                fireEvent.click(save);

                const editButtonAfterClickingSave = await waitForElement(()=> queryByText("Edit"));
                expect(editButtonAfterClickingSave).toBeInTheDocument();
            }) 
            it('returns to original displayname after changing the displayname and clickling cancel button', async ()=>{
                const {queryByText,container} = await setupForEdit();
                const input = container.querySelector('input');
                fireEvent.change(input,{target:{value:'display1-update'}})
                const cancelButton = queryByText("Cancel");
                fireEvent.click(cancelButton);

                const originalDisplayNameText = queryByText('display1@user1');
                expect(originalDisplayNameText).toBeInTheDocument();
            }) 
            it('returns to last updated displayname when displayname is changed another time but cancelled', async ()=>{
                const {queryByText,container} = await setupForEdit();
                let input = container.querySelector('input');
                fireEvent.change(input,{target:{value:'display1-update'}})
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockSuccesUpdateUser);
                const save = queryByText("Save");
                fireEvent.click(save);

                const editButtonAfterClickingSave = await waitForElement(()=> queryByText("Edit"));
                fireEvent.click(editButtonAfterClickingSave);

                input = container.querySelector('input');
                fireEvent.change(input,{target:{value:'display1-update2'}})

                const cancelButton = queryByText("Cancel");
                fireEvent.click(cancelButton);

                const lastSavedData = container.querySelector('h4');
                expect(lastSavedData).toHaveTextContent('display1-update@user1');
            }) 
            it('displays spinner when there is update user apicall', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockDelayedUpdateSuccess);
                const save = queryByText("Save");
                fireEvent.click(save);

                const spinner = queryByText('Loading...');
                expect(spinner).toBeInTheDocument();
            }) 
            it('disables save button when there is ongoing userupdate apicall', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockDelayedUpdateSuccess);
                const save = queryByText("Save");
                fireEvent.click(save);
                expect(save).toBeDisabled();;
            })
            it('disables cancel button when there is ongoing userupdate apicall', async ()=>{
                const {queryByText} = await setupForEdit();
                apiCalls.updateUser = jest.fn().mockResolvedValue(mockDelayedUpdateSuccess);
                const save = queryByText("Save");
                fireEvent.click(save);
                const cancelButton = queryByText("Cancel");
                expect(cancelButton).toBeDisabled();;
            })
            it('enables save button after updateuser api call fails',()=>{
                const {queryByText,container} = await setupForEdit();
                let input = container.querySelector('input');
                fireEvent.change(input,{target:{value:'display1-update'}})
                apiCalls.updateUser = jest.fn().mockRejectedValue(mockFailUpdateUser);

                const save = queryByText("Save");
                fireEvent.click(save);

                await WaitForDomChange();

                expect(save).not.toBeDisabled();
            }) */
        });
    describe('Lifecycle',()=>{
        it('calls getUser when it is rendered',()=>{
            apiCalls.getUser = jest.fn().mockResolvedValue(mockSuccesGetUser);
            setup({match});
            expect(apiCalls.getUser).toHaveBeenCalledTimes(1);
        })
        it('calls getUser with user1 when it is rendered with user1 in match',()=>{
            apiCalls.getUser = jest.fn().mockResolvedValue(mockSuccesGetUser);
            setup({match});
            expect(apiCalls.getUser).toHaveBeenCalledWith('user1');
        })
    })
})

console.error = () =>{}