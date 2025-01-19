import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link, useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import LoginPage from './LoginPage';
import SignupPage from './SignUpPage';
import Dashboard from './DashBoard';
import MainPage from './MainPage';
import CurrentTime from './CurrentTime';
import NewsPage from './NewsPage';
import AuthButtons from './AuthButtons';
import VisitorCount from './VisitorCount';
import AdminPanel from './AdminPanel';
import UpdateUser from './UpdateUser';
import Footer from './Footer';
import UpdateUserData from './updateUserData';
import NewsFormPage from "./NewsFormPage";

function App() {
  const [buttonsStatus, setButtonsStatus] = useState('');
  const [error, setError] = useState('');

  const getButtons = async () => {
    try {
      const response = await axios.get("http://localhost:8080/checklogin", {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json"
        }
      });
      setButtonsStatus(response.status); // Устанавливаем статус кнопок
    } catch (error) {
      setError("Не удалось загрузить кнопки");
      // console.error(error);
    }
  };



  function AppContent(){
    const location = useLocation();
    const navigate = useNavigate();

    const handleLogout = async () => {
      try {
        await axios.get("http://localhost:8080/logout", {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json"
          }
        });
        setButtonsStatus(''); // Сбрасываем статус кнопок после выхода
        navigate('/');
      } catch (error) {
        console.error("Ошибка при выходе:", error);
      }
    };

    useEffect(()=>{
      getButtons();
    }, [location])

    return (
      <>
        <div className="header d-flex justify-content-between align-items-center p-3">
          <div className="d-flex align-items-center">
            <Link to="/" className="btn btn-secondary">Главная</Link>
            <Link to="/news" className="btn btn-secondary mx-2">Новости</Link>
          </div>
          
          <div className="center-block">
            <CurrentTime />
            <VisitorCount />
          </div>

          <div>
            <AuthButtons buttonsStatus={buttonsStatus} />
          </div>
        </div>
        
        <main>
        <div className="d-flex justify-content-center align-items-center">
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/news" element={<NewsPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/dashboard" element={<Dashboard  onLogout={handleLogout}/>} />
            <Route path="/admin-panel" element={<AdminPanel />} />
            <Route path="/update/:id" element={<UpdateUser />}/>
            <Route path="/updateUser/:id" element={<UpdateUserData />}/>
            <Route path="/news/add" element={<NewsFormPage />} />
            <Route path="/news/:id/edit" element={<NewsFormPage />} />
          </Routes>
        </div>
        </main>
        <Footer/>
      </>
    );
  }
  return (
    <div className="App">
      <Router>
        <AppContent />
      </Router>
    </div>
  );
}

export default App;