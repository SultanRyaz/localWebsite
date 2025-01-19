import React from 'react';
import { Link } from 'react-router-dom';
import './css/AuthButtons.css';

const AuthButtons = ({ buttonsStatus }) => {
  return (
    <div>
      {buttonsStatus === 200 ? (
        <Link to="/dashboard" className="btn btn-secondary mx">Личный кабинет</Link>
      ) : (
        <div>
          <Link to="/login"className="btn btn-secondary mx">Вход</Link>
          <Link to="/signup" className="btn btn-secondary mx-2">Регистрация</Link>
        </div>
      )}
    </div>
  );
};

export default AuthButtons;