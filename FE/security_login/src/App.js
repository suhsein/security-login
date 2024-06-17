import './App.css';
import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

import JoinForm from './pages/Join';
import LoginForm from './pages/Login';
import Home from './pages/Home';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> 
        <Link to="/join">Join</Link>
        <Link to="/login">Login</Link>
      </nav>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/join" element={<JoinForm />}/>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
