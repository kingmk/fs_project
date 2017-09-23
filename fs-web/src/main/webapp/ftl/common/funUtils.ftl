<#--
  * 格式化数字  注意方法小数位会四舍五入
  -->
<#function formatNumber numVal=0 format="0.00" defaultVal="0.00">
	<#if numVal?exists>
		<#if numVal?is_number>
			<#return numVal?string(format)>
		<#else>
			<#return defaultVal>
		</#if>	
	<#else>
		<#return defaultVal>
	</#if>
</#function>
<#--
  * 格式化数字 取到格式化后数字 的整数位部分 注意方法小数位会四舍五入
  -->
<#function formatNumberInt numVal=0 format="0.00" defaultVal="0">
	<#assign val = formatNumber(numVal,format,defaultVal)>
	<#assign index = val?index_of(".")>
	<#if ( index<=0)>
		<#return val>
	<#else>
		<#return val?substring(0,index)>
	</#if>
</#function>

<#--
  * 格式化数字 取到格式化后数字 的小数位部分 注意方法小数位会四舍五入
  -->
<#function formatNumberDec numVal=0 format="0.00" defaultVal="00">
	<#if numVal==0>
			<#return defaultVal>
	</#if>
	<#assign val = formatNumber(numVal,format,defaultVal)>
	<#assign index = val?index_of(".")>
	<#if index==0>
		<#return defaultVal>
	<#else>
		<#return val?substring(index+1,val?length)>
	</#if>
</#function>

<#-- 百分数转换 -->
<#function percent numVal format="0.00">
	<#if numVal?has_content>
		<#if numVal?is_number>
			<#assign val = formatNumber(numVal*100,format) + "%">
			<#return val>
		<#else>
			<#return "--">
		</#if>	
	<#else>
		<#return "--">
	</#if>
</#function>

<#-- 日期格式化 -->
<#function formatDate sDate=null fmt="yyyy-MM-dd" defaultVal="--">
	<#if sDate ??>
		<#return sDate?string(fmt)>
	<#else>
		<#return defaultVal>
	</#if>
</#function>