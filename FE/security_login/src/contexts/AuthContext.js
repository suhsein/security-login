import { createContext, useContext, useState } from "react";

// 로그인 상태 전역적으로 쓰기 위해 context api 사용 -> prop drilling 을 피함
const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    // 기본적으로 로컬 스토리지의 access 값을 기준으로 setting
    const [isLoggedIn, setIsLoggedIn] = useState(!!window.localStorage.getItem('access'));
    const [loginUser, setLoginUser] = useState(window.localStorage.getItem('name'));

    return (
        <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn, loginUser, setLoginUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useLogin = () => useContext(AuthContext);
export default AuthProvider;