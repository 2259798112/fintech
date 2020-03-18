
//删除topN 默认保留最后5个
function removeTopN(n,list) {
    //console.log('removeTopN')
    let size = list.length;
    if (n == undefined || n >= size) { n = size - 20 }

    for (var i = n - 1; i >= 0; i--) {
        let _this = list[i]
        _this.parentNode.removeChild(_this)
    }
}

//聚焦到最后
function moveToLast(list) {
    console.log("moveToLast")
    for (var i = 0; i < list.length; i++) {
        if (i % 2==0) {
            if (list[i] !== null ){
                list[i].scrollIntoView()
                //ContentItem-actions
                if (list[i].querySelector('.ContentItem-actions') != null) {
                    list[i].querySelector('.ContentItem-actions').scrollIntoView();
                }
            }
        }
    }

    let last = list[list.length-1]
    if (last != null){
        if (last.querySelector('.RichContent') != null ) {
            last.querySelector('.RichContent').scrollIntoView();
        }
        if (last.querySelector('.ContentItem-actions') != null) {
            last.querySelector('.ContentItem-actions').scrollIntoView();
        }

    }
}

//监听dom 问题列表新增
function loadMore() {
    //// console.log('loadMore')
    //1,删除AD
    // removeAD();
    //2,获取所有问题
    var itemList = document.querySelector('#QuestionAnswers-answers').querySelectorAll('.List-item')
    console.log('itemList size = '+ itemList.length);
    parseAnswer(itemList);
    //3,删除topN
    removeTopN(undefined,itemList);
    //4,加载新数据
    moveToLast(itemList);
    console.log('answer size = '+ answerUrlList.length);
}

function parseAnswer(list){
    console.log("parseAnswer")
    var itemList = list

    for (var i = itemList.length - 1; i >= 0; i--) {
        let item = itemList[i];
        var author = item.querySelector('.ContentItem-meta');
        //校验重复问题
        if (author === null) {
            //console.log('重复节点')
            continue
        }

        var obj = new Object();

        //auther name
        var authorName = author.querySelector('meta[itemprop=name]').getAttribute('content');
        //console.log('回答者名字='+authorName)
        obj.authorName=authorName;
        //auther img
        var authorImg = author.querySelector('meta[itemprop=image]').getAttribute('content');
        //console.log('回答者头像='+authorImg)
        obj.authorImg=authorImg;
        //auther url
        var authorUrl = author.querySelector('meta[itemprop=url]').getAttribute('content');
        //console.log('回答者主页='+authorUrl)
        obj.authorUrl = authorUrl;

        item.firstChild.firstChild.remove();
        //answer url
        var answerUrl = item.querySelector('meta[itemprop=url]').getAttribute('content');
        //console.log('回答地址='+answerUrl)
        obj.answerUrl=answerUrl;
        //answer vote
        var upvoteCount = item.querySelector('meta[itemprop=upvoteCount]').getAttribute('content');
        //console.log('回答投票='+upvoteCount)
        obj.upvoteCount=upvoteCount;

        //answer content text number
        var wordCount = item.querySelector('.RichContent-inner').textContent.length;
        //console.log('回答字数='+wordCount)
        obj.wordCount=wordCount;

        //answer content img
        var imgCount = item.querySelector('.RichContent-inner').querySelectorAll('img').length
        //console.log('回答图片数='+imgCount)
        obj.imgCount=imgCount;

        //answer create
        var dateCreated = item.querySelector('meta[itemprop=dateCreated]').getAttribute('content');
        //console.log('回答创建时间='+dateCreated)
        obj.dateCreated=dateCreated;

        //answer modify
        var dateModified = item.querySelector('meta[itemprop=dateModified]').getAttribute('content');
        //console.log('回答修改时间='+dateModified)
        obj.dateModified=dateModified;

        //answer commentCount
        var commentCount = item.querySelector('meta[itemprop=commentCount]').getAttribute('content');
        //console.log('回答修改评论数='+commentCount)
        obj.commentCount=commentCount;

        //answer reward
        var reward = item.querySelectorAll('.Reward').length;
        //console.log('回答赞赏='+reward)
        obj.reward=reward;

        if (answerUrlList.indexOf(answerUrl) == -1) {
            answerUrlList.push(answerUrl)
            // answerList.push(obj)
        }
        //发送 ajax
        console.log(JSON.stringify(obj));
    }

}

function parseQuestion(){
    console.log("parseQuestion")
    var QuestionHeader = document.querySelector('.QuestionHeader');
    var topics = [];

    var topicList = QuestionHeader.querySelectorAll('.QuestionTopic');
    for (var i = topicList.length - 1; i >= 0; i--) {
        var topicName = topicList[i].textContent;
        var hrefs = topicList[0].querySelector('a').href.split('/');
        var topicId = hrefs[hrefs.length-1];
        topics.push(topicName+'='+topicId);
        //console.log('topicName='+topicName + " topicId="+topicId);
    }

    var questionTitle = QuestionHeader.querySelector('.QuestionHeader-title').textContent
    //console.log('questionTitle='+questionTitle)
    //QuestionHeader.querySelector('.QuestionHeader-side').textContent.replace(/,/g,'')

    var statusList = QuestionHeader.querySelector('.QuestionHeader-side').querySelectorAll('.NumberBoard-itemInner');
    var followers = statusList[0].getElementsByTagName('strong')[0].getAttribute('title')
    //console.log('关注人数='+followers)
    var views = statusList[1].getElementsByTagName('strong')[0].getAttribute('title')
    //console.log('浏览人数='+views)
    var questionComment = QuestionHeader.querySelector('.QuestionHeader-footer').querySelector('.QuestionHeader-Comment').textContent.replace(/,/,'').split(' ')[0]
    //console.log('问题评论数='+questionComment)

    var obj = new Object();
    obj.topics = topics;
    obj.questionTitle = questionTitle;
    obj.followers=followers;
    obj.views=views;
    obj.questionComment = questionComment;
    console.log(JSON.stringify(obj));
}


let MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;

let observer_box = new MutationObserver(loadMore);

observer_box.observe(document.querySelector('#QuestionAnswers-answers').querySelector('.List-item').parentElement, {
    childList: true
});

let answerUrlList = [];
parseQuestion();
loadMore();

