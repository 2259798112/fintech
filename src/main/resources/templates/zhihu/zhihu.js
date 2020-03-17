// 删除广告
function removeAD() {
    let rList = document.querySelectorAll('.TopstoryItem--advertCard');
    if (rList != null && rList.length > 0) {
        for (let i = rList.length - 1; i >= 0; i--) {
            let _this = rList[i]
            _this.parentNode.removeChild(_this)
        }
    }
}



//获取问题列表
function getQuestionList() {
    console.log('getQuestionList');
    let questionList = document.querySelectorAll('.TopstoryItem-isRecommend');

    let list = [];

    if (questionList != null && questionList.length > 0) {

        for (let i = questionList.length - 1; i >= 0; i--) {
            let _this = questionList[i]
            //class ContentItem-title 标题
            let h = _this.querySelector(".ContentItem-title");
            let metaList = h.querySelectorAll('meta');
            if (metaList != null && metaList.length == 2) {
                let m0 = metaList[0].getAttribute('content');
                if (qidlist.indexOf(m0) == -1) {
                    qidlist.push(m0);
                    console.log(metaList[0].getAttribute('itemprop') + '=' + m0);
                    console.log(metaList[1].getAttribute('itemprop') + '=' + metaList[1].getAttribute('content'));
                }
            }
        }
    }

}

//聚焦到最后
function moveToLast() {
    // console.log("moveToLast")
    let questionList = document.querySelectorAll('.TopstoryItem-isRecommend');
    questionList[questionList.length - 10].scrollIntoView();
    questionList[questionList.length - 1].scrollIntoView();
}

//删除topN 默认保留最后5个
function removeTopN(n) {
    // console.log('removeTopN')
    let questionList = document.querySelectorAll('.TopstoryItem-isRecommend');
    let size = questionList.length;
    if (n == undefined || n >= size) { n = size - 20 }

    for (var i = n - 1; i >= 0; i--) {

        let _this = questionList[i]
        _this.parentNode.removeChild(_this)
    }
    // console.log("questionList length " + questionList.length);
    // console.log("questionList remove top " + n);
}

//监听dom 问题列表新增
function loadMore() {
    // console.log('loadMore')
    //1,删除AD
    removeAD();
    //2,获取所有问题
    getQuestionList();
    //3,删除topN
    removeTopN();
    //4,加载新数据
    moveToLast();

    console.log("size="+qidlist.length);

}

function run() {

    let MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;

    let observer_box = new MutationObserver(loadMore);

    let ele = document.querySelector('#TopstoryContent').querySelector('.Topstory-recommend').firstElementChild;

    observer_box.observe(ele, {
        childList: true
    });

    let qidlist = [];
    loadMore();

}
