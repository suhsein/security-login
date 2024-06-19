import './App.css';

import AuthProvider from './contexts/AuthContext';
import NavBar from './components/NavBar';
import MyRoutes from './routes/MyRoutes';

function App() {
  return (
    <div className="App">
      <AuthProvider>
        <NavBar />
        <MyRoutes />
      </AuthProvider>
    </div>
  );
}

export default App;
