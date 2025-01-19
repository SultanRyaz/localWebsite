import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './css/NewsForm.css';

const NewsForm = ({ onNewsAdded, selectedNews, onNewsUpdated, userId }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [image, setImage] = useState(null);
  const [imageUrl, setImageUrl] = useState(null);
  const [deleteImage, setDeleteImage] = useState(false); // Состояние для чекбокса

  useEffect(() => {
    if (selectedNews) {
      setTitle(selectedNews.title);
      setContent(selectedNews.content);
      setImageUrl(selectedNews.imagePath);
        setDeleteImage(false); // Сбросить чекбокс при загрузке данных
    } else {
        setTitle("");
        setContent("");
        setImageUrl(null);
      setDeleteImage(false);
    }
  }, [selectedNews]);

  const handleImageChange = (e) => {
      setImage(e.target.files[0]);
    setDeleteImage(false); // Снять галку, если добавили картинку
  };

    const handleRemoveImageChange = (e) => {
        setDeleteImage(e.target.checked); // Обновить состояние чекбокса
        if(e.target.checked) {
            setImage(null) // Отменить загрузку, если удаляем
        }
    }

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content);

    if (image) {
      formData.append('image', image);
    }
    if(deleteImage){
      formData.append('deleteImage', true);
    }
    try {
          let response;
          if (selectedNews) {
            response = await axios.put(`http://localhost:8080/api/news/${selectedNews.id}`, formData, {
              withCredentials: true,
              headers: {
                 'Content-Type': 'multipart/form-data' 
              },
               params:{
                userId: userId
               }
            });
            onNewsUpdated(response.data);
        } else {
            response = await axios.post('http://localhost:8080/api/news', formData, {
              withCredentials: true,
              headers: {
                'Content-Type': 'multipart/form-data'
              },
              params:{
                userId: userId
              }
            });
            onNewsAdded(response.data);
          }
    } catch (error) {
      console.error('Error submitting form:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} required/>
      <textarea placeholder="Content" value={content} onChange={(e) => setContent(e.target.value)} required/>

      {imageUrl && (
        <div className="image-preview">
            <label>
                <input
                    type="checkbox"
                    checked={deleteImage}
                    onChange={handleRemoveImageChange}
                />
              Удалить картинку
            </label>
        </div>
      )}

      <input type="file" accept="image/*" onChange={handleImageChange} />
      <button type="submit">{selectedNews ? "Изменить" : "Добавить"}</button>
    </form>
  );
};

export default NewsForm;