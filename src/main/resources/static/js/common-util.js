function gettime(t) {
    let _time = new Date(t);
    let year = _time.getFullYear();//2017
    let month = _time.getMonth() + 1;//7
    let date = _time.getDate();//10
    let hour = _time.getHours();//10
    hour = hour < 10 ? '0' + hour : hour;
    let minute = _time.getMinutes();//56
    minute = minute < 10 ? '0' + minute : minute;
    let second = _time.getSeconds();//15
    second = second < 10 ? '0' + second : second;
    return year + "年" + month + "月" + date + "日   " + hour + ":" + minute + ":" + second;//这里自己按自己需要的格式拼接
}