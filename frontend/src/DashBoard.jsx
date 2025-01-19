// WelcomeDashboard.js
import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/DashBoard.css';

function WelcomeDashboard({onLogout}) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('');
    const [id, setId] = useState('');
    const [avatar, setAvatar] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const [showFavorites, setShowFavorites] = useState(false);
    const [favorites, setFavorites] = useState([]);
    const [games, setGames] = useState([]);

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get('http://localhost:8080/dashboard', {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
                setId(response.data.id);
                setData(response.data.id);
                fetchGames();
                fetchFavorites(response.data.id);
            } catch (error) {
                setError('Не удалось загрузить информацию о пользователе');
                console.error(error);
            }
        };

        const setData = async (id) => {
            try {
                const response = await axios.get(`http://localhost:8080/getinfoUser/${id}`, {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
                setUsername(response.data.username);
                setEmail(response.data.email); 
                setRole(response.data.role);
                setAvatar(response.data.avatar);
              
            } catch (error) {
                setError('Не удалось загрузить информацию о пользователе4');
                console.error(error);
            }
        };
        fetchUserInfo();
    }, []);

    const handleAdminPanelClick = () => {
        navigate('/admin-panel'); // Путь на панель администратора
    };

    const handleUserPanelClick = () => {
        navigate(`/updateUser/${id}`);
     };

     const handleToggleFavorites = () => {
        setShowFavorites(!showFavorites);
    };

    const fetchFavorites = async (id) => {
        try {
            const response = await axios.get(`http://localhost:8080/${id}/favorites`, {
                withCredentials: true,
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
            setFavorites(response.data.map(game => game.id));
        } catch (error) {
            console.error("Ошибка при загрузке избранных игр:", error);
             setError("Ошибка при загрузке избранных игр. Попробуйте позже.");
        }
    };

    const fetchGames = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/games', {
                withCredentials: true,
                 headers: {
                     "Content-Type": "application/json"
                 }
                });
            setGames(response.data);
        } catch (e) {
            console.error("Ошибка при загрузке игр:", e);
            setError("Ошибка при загрузке данных. Попробуйте позже.");
        } 
    };

    const handleRemoveFromFavorites = async (gameId, id) => {
        try {
            await axios.delete(`http://localhost:8080/${id}/favorites/${gameId}`, {
                withCredentials: true,
                    headers: {
                        "Content-Type": "application/json"
                    }
                }); //Заменить 1 на получение id авторизованного пользователя
             fetchFavorites(id);
        } catch (error) {
          console.error("Failed to remove from favorites:", error);
           setError("Ошибка при удалении из избранного. Попробуйте позже.");
        }
    };

    return (
        <div>
        <div className="d-flex justify-content-center align-items-center">
            <div className="border rounded-lg p-4" style={{ width: '500px', height: '500px', }}>
                <h2 className="text-center">Добро пожаловать!</h2>
                {error ? (
                    <p className="text-danger text-center">{error}</p>
                ) : (
                    <>
                        <div className="text-center">
                            {avatar && <img src={avatar} alt="User Avatar" style={{ width: '100px', height: '100px', borderRadius: '60px' }} />}
                        </div>
                        
                        <p className="text-center">ID: {id}</p>
                        <p className="text-center">Имя пользователя: {username}</p>
                        <p className="text-center">Электронная почта: {email}</p>
                        <p className="text-center">Роль: {role}</p>
                    </>
                )}
                <div className="text-center">
                {
                    role === "ADMIN"?(
                        <button type="button" className="btn btn-secondary" onClick={handleAdminPanelClick}>
                            Панель администратора
                        </button>
                    ):(
                        <div className="text-center">
                            <button type="button" className="btn btn-secondary mt-3" onClick={handleUserPanelClick}>
                                Изменить
                            </button>
                        </div>
                    )
                }
                </div>
                <div className="text-center">
                    <button type="button" className="btn btn-danger mt-3" onClick={onLogout}>
                        Выйти
                    </button>
                </div>
            </div>
            </div>
            <div>
            <div className="game-header1">
               <button className="profile-button" onClick={handleToggleFavorites}>
                  {showFavorites ? "Скрыть избранное" : "Показать избранное"}
                </button>
            </div>
             {showFavorites && (
                
                <div className="favorites-container">
                    <center>
                  <h2>Избранные игры</h2>
                  {favorites.length === 0 ? (
                    <p>У вас пока нет избранных игр.</p>
                  ) : (
                    <ul className="game-list">
                      {games.filter((game) => favorites.includes(game.id)).map((game) => (
                        <li key={game.id} className="game-item">
                           <img src={`http://localhost:8080/images/${game.image}`} alt={game.name} className="game-image"/>
                          <div className="game-info">
                              <h3>{game.name}</h3>
                              <p>{game.description}</p>
                                <button
                                  className="remove-favorite-button"
                                    onClick={() => handleRemoveFromFavorites(game.id, id)}
                                  >
                                      Убрать из избранного
                                </button>
                          </div>
                        </li>
                      ))}
                    </ul>
                  )}
                  </center>
                 </div>
             )}
        </div>
        </div>
    );
}

export default WelcomeDashboard;