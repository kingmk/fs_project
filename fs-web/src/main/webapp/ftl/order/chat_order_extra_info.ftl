<div class="user-data-wrapper bgmask-wrap">
		<div class="bgmask-close"></div>
		<div class="user-data-header">
			${body.goodsName} | ¥ ${body.payRmbAmtDesc}
		</div>
		<div class="bgmask-body">
			<ul>
			<#if body.orderExtraInfo ??>
				<#list body.orderExtraInfo as item>
          <#if item.mobile?? && item.mobile!=''>
          <li>
            <div class="tit">您的联系方式</div>
            <div class="txt">${item.mobile}</div>
          </li>
          </#if>
          <#if item.newAddress?? && item.newAddress!=''>
            <#if item_index==0>
            <li>
              <div class="tit">新宅地址</div>
              <div class="txt">${item.newAddress}</div>
            </li>
            </#if>
          </#if>
          <#if item.completedTime?? && item.completedTime!=''>
            <#if item_index==0>
            <li>
              <div class="tit">新宅落成时间</div>
              <div class="txt">${item.completedTime}</div>
            </li>
            </#if>
          </#if>
          <#if (item.expectMoveDateBegin?? && item.expectMoveDateBegin!='') || (item.expectMoveDateEnd?? && item.expectMoveDateEnd!='')>
            <#if item_index==0>
            <li>
              <div class="tit">期望乔迁时间</div>
              <div class="txt">
                <#if (item.expectMoveDateBegin?? && item.expectMoveDateBegin!='') && (item.expectMoveDateEnd?? && item.expectMoveDateEnd!='')>
                  ${item.expectMoveDateBegin} 至 ${item.expectMoveDateEnd}
                <#elseif (item.expectMoveDateBegin?? && item.expectMoveDateBegin!='') && !(item.expectMoveDateEnd?? && item.expectMoveDateEnd!='')>
                 ${item.expectMoveDateBegin} 起
                <#elseif !(item.expectMoveDateBegin?? && item.expectMoveDateBegin!='') && (item.expectMoveDateEnd?? && item.expectMoveDateEnd!='')>
                 ${item.expectMoveDateEnd} 前
                </#if>
              </div>
            </li>
            </#if>
          </#if>
					<#if item.realName?? && item.realName!=''>
					<li>
						<div class="tit"><#if item_index==1>对象的<#else>您的</#if>中文姓名</div>
						<div class="txt">${item.realName}</div>
					</li>
          <#elseif body.goodsName != "个人起名" && body.goodsName != "办公风水" && body.goodsName != "住宅风水"  && body.goodsName != "其他">
            <div class="tit"><#if item_index==1>对象的<#else>您的</#if>中文姓名</div>
            <div class="txt">未填写</div>
					</#if>
          <#if item.onceName?? && item.onceName!=''>
          <li>
            <div class="tit">您的曾用名</div>
            <div class="txt">${item.onceName}</div>
          </li>
          </#if>
					<#if item.englishName?? && item.englishName!=''>
					<li>
						<div class="tit"><#if item_index==1>对象的<#else>您的</#if>英文姓名</div>
						<div class="txt">${item.englishName}</div>
					</li>
					</#if>
					<#if item.sex?? && item.sex!=''>
					<li>
						<div class="tit">性别</div>
						<div class="txt"><#if item.sex=="F">女<#else>男</#if></div>
					</li>
					</#if>
					<#if item.birthDate??  && item.birthDate!=''>
					<li>
						<div class="tit">阳历生日</div>
						<div class="txt">${item.birthDate}</div>
					</li>
					</#if>
<!-- 					<#if item.nlBirthDateDesc??  && item.nlBirthDateDesc!=''>
					<li>
						<div class="tit">农历生日</div>
						<div class="txt">${item.nlBirthDateDesc}</div>
					</li>
					</#if> -->
          <#if item.birthTimeText?? && item.birthTimeText!=''>
          <li>
            <div class="tit">出生时间</div>
            <div class="txt">${item.birthTimeText}</div>
          </li>
          </#if>
<!-- 					<#if item.bazi??  && item.bazi!=''>
					<li>
						<div class="tit">八字</div>
						<div class="txt">${item.bazi}</div>
					</li>
					</#if> -->
					<#if item.birthAddress?? && item.birthAddress!=''>
					<li>
						<div class="tit">出生地</div>
						<div class="txt">${item.birthAddress}</div>
					</li>
					</#if>
					<#if item.marriageStatus??  && item.marriageStatus!=''>
					<li>
						<div class="tit">婚姻状况</div>
						<div class="txt">
						<#if item.marriageStatus=="single">单身
						<#elseif item.marriageStatus=="celibate">独身
						<#elseif item.marriageStatus=="married">已婚
						<#elseif item.marriageStatus=="divorce">离异
						<#elseif item.marriageStatus=="widowed">丧偶
						<#else>再婚
						</#if></div>
					</li>
					</#if>
					<#if item.familyRank?? && item.familyRank!=''>
					<li>
						<div class="tit">家中排行</div>
						<div class="txt">${item.familyRank}</div>
					</li>
					</#if>
          <#if item.brotherName?? && item.brotherName!=''>
          <li>
            <div class="tit">哥哥姓名</div>
            <div class="txt">${item.brotherName}</div>
          </li>
          </#if>
          <#if item.sisterName?? && item.sisterName!=''>
          <li>
            <div class="tit">姐姐姓名</div>
            <div class="txt">${item.sisterName}</div>
          </li>
          </#if>
          <#if item.curComName?? && item.curComName!=''>
          <li>
            <div class="tit">企业现用名</div>
            <div class="txt">${item.curComName}</div>
          </li>
          </#if>
          <#if item.establishedDate??  && item.establishedDate!=''>
          <li>
            <div class="tit">成立时间</div>
            <div class="txt">${item.establishedDate}</div>
          </li>
          </#if>
          <#if item.industry?? && item.industry!=''>
          <li>
            <div class="tit">所处行业</div>
            <div class="txt">${item.industry}</div>
          </li>
          </#if>
          <#if item.scopeOfBusiness?? && item.scopeOfBusiness!=''>
          <li>
            <div class="tit">经营范围</div>
            <div class="txt">${item.scopeOfBusiness}</div>
          </li>
          </#if>
          <#if item.comAddress?? && item.comAddress!=''>
          <li>
            <div class="tit">所在地</div>
            <div class="txt">${item.comAddress}</div>
          </li>
          </#if>
          <#if (item.expectMarriageDateBegin?? && item.expectMarriageDateBegin!='') || (item.expectMarriageDateEnd?? && item.expectMarriageDateEnd!='') >
            <#if item_index==1>
            <li>
              <div class="tit">期望结婚时间</div>
              <div class="txt">
              <#if (item.expectMarriageDateBegin?? && item.expectMarriageDateBegin!='')  && (item.expectMarriageDateEnd?? && item.expectMarriageDateEnd!='')>
                ${item.expectMarriageDateBegin} 至 ${item.expectMarriageDateEnd}
              <#elseif (item.expectMarriageDateBegin?? && item.expectMarriageDateBegin!='')  && !(item.expectMarriageDateEnd?? && item.expectMarriageDateEnd!='')>
               ${item.expectMarriageDateBegin} 后
              <#elseif !(item.expectMarriageDateBegin?? && item.expectMarriageDateBegin!='')  && (item.expectMarriageDateEnd?? && item.expectMarriageDateEnd!='')>
               ${item.expectMarriageDateBegin} 前
              </#if>
              </div>
            </li>
            </#if>
          </#if>
          <#if item.fetusNum?? && item.fetusNum!=''>
            <#if item_index==1>
            <li>
              <div class="tit">第几胎</div>
              <div class="txt">${item.fetusNum}</div>
            </li>
            </#if>
          </#if>
				</#list>
			</#if>
			</ul>
		</div>
	</div>