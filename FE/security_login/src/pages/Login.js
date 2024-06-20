import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useLogin } from '../contexts/AuthContext';

const LoginForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const {setIsLoggedIn, setLoginUser} = useLogin();

    const prevUrl = location.state || "/";

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const fetchLogin = async (credentials) => {
        try {
            const response = await fetch("http://localhost:8080/login", {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(credentials),
            });

            if (response.ok) {
                alert('Login successful');

                const data = await response.json();
                const { name } = data;

                window.localStorage.setItem("access", response.headers.get("access"));
                window.localStorage.setItem("name", name);
                
                setIsLoggedIn(true);
                setLoginUser(name);
                
                // 로그인 완료 후, 이전 요청이 존재하면 이전 요청으로 이동
                navigate(prevUrl, { replace: true });
            } else {
                alert('Login failed');
            }
        } catch (error) {
            console.log('error: ', error)
        }
    };

    const loginHandler = async (e) => {
        e.preventDefault();
        const credentials = { username, password };
        fetchLogin(credentials);
    }

    return (
        <div className='login'>
            <h1>Login</h1>
            <form method='post' onSubmit={loginHandler}>
                <p><span className='label'>Username</span><input className='input-class' type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder='username' /></p>
                <p><span className='label'>Password</span><input className='input-class' type="password" autoComplete='off' value={password} onChange={(e) => setPassword(e.target.value)} placeholder='password' /></p>
                <input type="submit" value="Login" className='form-btn' />
            </form>

            <div className='social-login'>
                <h2>소셜 로그인</h2>
                <div>
                    <a href="http://localhost:8080/oauth2/authorization/naver"><img className='social-icon' src="naver_icon.png" alt="naver" /></a>
                    <a href="http://localhost:8080/oauth2/authorization/google"><img className='social-icon' src="google_icon.png" alt="google" /></a>
                    <a href="http://localhost:8080/oauth2/authorization/github"><img className='social-icon' src="github_icon.png" alt="github" /></a>
                </div>
            </div>
        </div>
    );
};

export default LoginForm;