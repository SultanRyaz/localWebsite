import React, { useState, useEffect } from 'react';
import './css/MainPage.css';
import axios from 'axios';

function MainPage() {
    const [games, setGames] = useState([]);
    const [newGameName, setNewGameName] = useState('');
    const [newGameImage, setNewGameImage] = useState(null);
    const [newGameDescription, setNewGameDescription] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [role, setRole] = useState('');
    const [favorites, setFavorites] = useState([]);
    const [UserId, setUserId] = useState('');

    useEffect(() => {
        fetchUserInfo();
    }, []);

    const fetchUserInfo = async () => {
         try {
             const response = await axios.get('http://localhost:8080/dashboard', {
                 withCredentials: true,
                 headers: {
                     "Content-Type": "application/json"
                 }
             });
             setRole(response.data.role);
             setUserId(response.data.id);
             fetchGames();
             fetchFavorites(response.data.id);
         } catch (error) {
            console.error('Failed to fetch user info', error);
            setError('Для просмотра войдите или зарегистрируйтесь!');
         }
    };
    const fetchGames = async () => {
        setLoading(true);
        setError(null);

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
        } finally {
            setLoading(false);
        }
    };
    const fetchFavorites = async (UserId) => {
        try {
            const response = await axios.get(`http://localhost:8080/${UserId}/favorites`, {
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

     const handleAddToFavorites = async (gameId, UserId) => {
           try {
              const response = await axios.post(`http://localhost:8080/${UserId}/favorites`, {gameId: gameId}, {withCredentials: true}); 
              if (response.status === 200) {
                  fetchFavorites(UserId);
              } else {
                console.error(`Failed to add to favorites, status: ${response.status}`);
                 setError("Ошибка при добавлении в избранное. Попробуйте позже.")
              }

           } catch (error) {
            console.error("Failed to add to favorites:", error);
              setError("Ошибка при добавлении в избранное. Попробуйте позже.");
           }
       };
      const handleRemoveFromFavorites = async (gameId, UserId) => {
        try {
            await axios.delete(`http://localhost:8080/${UserId}/favorites/${gameId}`, {withCredentials: true});
             fetchFavorites(UserId);
        } catch (error) {
          console.error("Failed to remove from favorites:", error);
           setError("Ошибка при удалении из избранного. Попробуйте позже.");
        }
    };

       const isGameFavorite = (gameId) => {
           return favorites.includes(gameId);
       };

    const handleAddGame = () => {
        setShowForm(true);
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setNewGameImage(file);
    };

    const handleFormSubmit = async (e) => {
         e.preventDefault();

        if (newGameName && newGameImage && newGameDescription) {
            setLoading(true);
             setError(null)
            try {
                const formData = new FormData();
                formData.append('name', newGameName);
                formData.append('description', newGameDescription);
                formData.append('image', newGameImage);

                await axios.post('http://localhost:8080/api/games', formData, {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                 fetchGames();
                 setNewGameName('');
                 setNewGameImage(null);
                 setNewGameDescription('');
                 setShowForm(false);
            } catch (e) {
              console.error("Ошибка при добавлении игры:", e);
              setError("Ошибка при добавлении данных. Попробуйте позже.");
            } finally {
                setLoading(false);
            }

        } else {
            alert("Пожалуйста, заполните все поля и выберите изображение.");
        }
    };
    const handleFormCancel = () => {
        setShowForm(false);
        setNewGameName('');
        setNewGameImage(null);
        setNewGameDescription('');
    };
    const handleDeleteGame = async (id) => {
       setLoading(true);
       setError(null);

       try {
           await axios.delete(`http://localhost:8080/api/games/${id}`, {
            withCredentials: true,
                 headers: {
                     "Content-Type": "application/json"
                 }
                });
           fetchGames();
       } catch (e) {
           console.error("Ошибка при удалении игры:", e);
           setError("Ошибка при удалении данных. Попробуйте позже.");
       } finally {
           setLoading(false);
       }
    };

    return (
        <div>
            <div className="game-header">
              <h2>Игры</h2>
              {role === "ADMIN" ? (
                  <button className="add-game-button" onClick={handleAddGame}>
                      +
                  </button>
              ) : null}
            </div>
            {showForm && (
                <form className="add-game-form" onSubmit={handleFormSubmit}>
                    <input
                        type="text"
                        placeholder="Название игры"
                        value={newGameName}
                        onChange={(e) => setNewGameName(e.target.value)}
                        required
                    />
                    <textarea
                        placeholder="Описание игры"
                        value={newGameDescription}
                        onChange={(e) => setNewGameDescription(e.target.value)}
                        required
                    />
                    <input
                        type="file"
                        onChange={handleImageChange}
                        accept="image/*"
                        required
                    />
                    <button type="submit">Добавить игру</button>
                    <button type="button" onClick={handleFormCancel}>Отмена</button>
                </form>
            )}
             {error && <p style={{color: "red"}}>{error}</p>}
            {!loading && !error && (
                <div className="game-list">
                    {games.map((game) => (
                        <div key={game.id} className="game-item">
                            <img src={`http://localhost:8080/images/${game.image}`} alt={game.name} className="game-image"/>
                            <div className="game-info">
                                <h3>{game.name}</h3>
                                <p>{game.description}</p>
                                <div className="game-actions">
                                     {!isGameFavorite(game.id) ? (
                                       <button onClick={() => handleAddToFavorites(game.id, UserId)}>
                                           Добавить в избранное
                                      </button>
                                 ) : (
                                     <button onClick={() => handleRemoveFromFavorites(game.id, UserId)}>
                                          Убрать из избранного
                                     </button>
                                     )}
                                     {role === "ADMIN" && (
                                         <button className="delete-game-button" onClick={() => handleDeleteGame(game.id)}>Удалить</button>
                                     )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default MainPage;