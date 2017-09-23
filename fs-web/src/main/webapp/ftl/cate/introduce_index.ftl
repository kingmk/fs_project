
 <#import "/common/host.ftl" as host>
 <#import "/common/funUtils.ftl" as funUtils>
 <html>
 <head>
  <meta charset="UTF-8">
    <title>${zxCateBean.name}</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/consultation.css?${host.version}">
    <link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
    <script src="../static/js/components.js?${host.version}"></script>
 </head>
 <body id='consultation-detail'>
 <div class="header">
        <img src="${host.img}/images/sortdetail/${zxCateBean.id}.jpg?${host.version}">
        <#-- <img src="${host.img}/images/sortdetail/transparent.png?${host.version}">
        <div class="title">${zxCateBean.name}</div>
        <div class="text">${zxCateBean.description}</div> -->
    </div>
    <div class="content">
        <div class="content-title">推荐老师</div>
        <div class="content-list">
        <#list result.body.data as item>
            <a class="content-item"  href="${host.base}/usr/search/master_detail?masterInfoId=${item.masterInfoId}&zxCateId=${item.zxCateId}&enterType=sourceCateIntro">
                <div class="left">
                    <img class="img" src="${item.masterHeadImgUrl}">
                    <div class="name">${item.masterNickName}</div>
                     <div class="star">
                    	<div class="star_show"><p style="width: ${item.score * 66 /2/20}rem"></p></div>
                    </div>
                </div>
                <div class="right">
                    <div class="price"><span class='small'>¥ </span>${item.amtDesc}</div>
                    <ul class="incoming-box">
                       <#if item.isCertificated == 'Y'>
                           <li class="red">实名认证</li>
                        </#if>
                        <#if  item.isTranSecuried == 'Y'>
                           <li class="yellow">担保交易</li>
                        </#if>
                        <#if item.isSignOther == 'N'>
                           <li class="blue">独家合作</li>
                        </#if>
                    </ul>
                    <div class="info">
                       ${item.experience}
                    </div>
                </div>
            </a>
        </#list>
        </div>
    </div>
    <div class="list" style="padding-bottom: 3.5rem;">
        <div class="list-body">
            <a class="list-item" href = '${host.base}/usr/search/master_nav?zxCateId=${zxCateBean.id}'>
                <div class="list-label">查看全部老师</div>
                <div class="list-arrow"></div>
            </a>
        </div>
    </div>
 </body>
 </html>
