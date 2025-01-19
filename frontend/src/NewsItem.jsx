import React from 'react';
import { Link } from 'react-router-dom';
import './css/NewsItem.css'

const NewsItem = ({ news, onDelete, role }) => {
  const imagePath = news.imagePath ? `http://localhost:8080/images/${news.imagePath}` : null;

  return (
        <div className = "news-item" style={{border: '1px solid #ccc', padding: '10px', margin: '10px'}}>
            <h3>{news.title}</h3>
            <p>{news.content}</p>
            {imagePath && <img src={imagePath} alt={news.title}/>}
            <p>Автор: {news.author}</p>
            <p>Дата: {new Date(news.date).toLocaleDateString()}</p>
            {
                    role === "MODER"?(
                        <>
                        <Link to={`/news/${news.id}/edit`}><button class='button_red'>Редактировать</button></Link>
                        <button class = 'button_del' onClick={() => onDelete(news.id)}>Удалить</button>
                        </>
                    ):(
                        null
                    )
                }
             
        </div>
    );
};

export default NewsItem;