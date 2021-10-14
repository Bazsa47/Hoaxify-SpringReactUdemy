const initialState = {
    id:0,
    username:'',
    displayname:'',
    image:'',
    password:'',
    isLoggedIn: false
};

//az actionök hogyan változtatják meg a statet
export default function authReducer(state = initialState, action){
    if(action.type==='logout-success'){
        return {...initialState};
    }
    if(action.type==='login-success'){
        return {
            ...action.payload,
            isLoggedIn: true
        };
    }
    return state;
}