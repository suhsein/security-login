import logo from './logo.svg';
import './App.css';
import { useState } from 'react';

const LoginForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const loginHandler = async (e) => {
    e.preventDefault();
    const credentials = {username, password};
    fetchLogin(credentials);
  }
  
  return (<form method='post' onSubmit={loginHandler}>
    <p>Username : <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder='username'/></p>
    <p>Password : <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}  placeholder='password'/></p>
    <input type="submit" value="Login"/>
  </form>);
}

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

    if(response.ok){
      console.log('Login successful');
    } else {
      console.log('Login failed');
    }
  } catch(error) {
    console.log('error: ', error)
  }
}


function App() {
 
  return (
    <div className="App">
      <LoginForm></LoginForm>
    </div>
  );
}

export default App;
