import axios from 'axios';
import React, { useEffect, useState } from 'react';

function CurrentTime() {
    const [time, setTime] = useState(null);
    const [error, setError] = useState('');

    // Функция для первоначальной загрузки времени с сервера
    const fetchInitialTime = async () => {
        try {
            const response = await axios.get("http://localhost:8080/time");
            const parsedTime = parseTime(response.data);
            setTime(parsedTime); // Устанавливаем время при успешном ответе
        } catch (error) {
            setError("Ошибка при загрузке времени");
        }
    };

    const parseTime = (timeString) => {
        const [datePart, timePart] = timeString.split(" ");
        const [day, month, year] = datePart.split(".");
        const [hours, minutes, seconds] = timePart.split(":");
        return new Date(`${year}-${month}-${day}T${hours}:${minutes}:${seconds}`);
    }

    const updateTime = () => {
        if (time) {
            setTime(new Date(time.getTime() + 1000));
        }
    }

    const formatTime = (timeObject) => {
        if (!timeObject) return "";
        return timeObject.toLocaleString("ru-RU", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        });
    }

    useEffect(() => {
        fetchInitialTime();
    }, []);

    useEffect(() => {
        if (time) {
            const interval = setInterval(updateTime, 1000);
            return () => clearInterval(interval);
        }
    }, [time]);

    return (
        <div>
            {error && <div>{error}</div>}
            <div>{formatTime(time)}</div>
        </div>
    );
}

export default CurrentTime;