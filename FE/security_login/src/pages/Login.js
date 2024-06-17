import React, { useState } from 'react';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const loginHandler = async (e) => {
        e.preventDefault();
        const credentials = { username, password };
        fetchLogin(credentials);
    }

    return (
        <div>
            <h1>Login</h1>
            <form method='post' onSubmit={loginHandler}>
                <p>Username : <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder='username' /></p>
                <p>Password : <input type="password" autoComplete='off' value={password} onChange={(e) => setPassword(e.target.value)} placeholder='password' /></p>
                <input type="submit" value="Login" />
            </form>

            <div className='socialLogin'>
                <h3>소셜 로그인</h3>
                <div>
                    <a href="http://localhost:8080/oauth2/authorization/naver">Naver</a>
                    <a href="http://localhost:8080/oauth2/authorization/google">Google</a>
                    <a href="http://localhost:8080/oauth2/authorization/github">Github</a>
                </div>
            </div>
        </div>
    );
};

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
            const { username } = data;

            window.localStorage.setItem("access", response.headers.get("access"));
            window.localStorage.setItem("username", username);

            window.location.href = "/";
        } else {
            alert('Login failed');
        }
    } catch (error) {
        console.log('error: ', error)
    }
};

export default LoginForm;