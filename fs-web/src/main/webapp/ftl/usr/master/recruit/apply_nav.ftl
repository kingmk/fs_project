<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>填写个人资料</title>
	<script src="${host.js}/js/rem.js?${host.version}"></script>
	<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
	<script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
	<link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
	<link rel="stylesheet" href="${host.css}/css/personal_data.css?${host.version}">
	<script src="${host.js}/js/cityDate.js?${host.version}"></script>
	<script>
		var hostImg = '${host.img}'
	</script>
	<script src="${host.js}/js/common.js?${host.version}"></script>
	<script src="${host.js}/js/fill_personal_data.js?${host.version}"></script>
	<style>
		.onError{display: none;}
		.forword{float:left; width:35%; background:#fff; color: #3f3f3f!important;}
		#next2{width: 65%}
		.a-upload-tips{height: 7.5rem!important;}
		.a-upload-tips .tips-add{font-size: 2.4rem; line-height: 1;}
	</style>
	<script>
		$(function(){
			$("#forword").on('click', function(){
				$("#personalData").show();
				$("#personalInfo").hide();
			})
		})
	</script>
</head>
<body>
<div>
	<form id="form" enctype="multipart/form-data">

		<div id="personalData">
			<div class="list form">
				<div class="list-header">个人信息</div>
				<div class="list-body">
					<div class="list-item">
						<div class="list-label">姓名</div>
						<div class="list-control">
							<input type="text" placeholder="请输入身份证上的姓名" name="realName" id="realName"/>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">现居住地</div>
						<div class="list-control">
							<input type="text" placeholder="未选择" name="liveAddress" id="liveAddress" value='上海市'/>
						</div>
						<div class="list-arrow"></div>
					</div>
					<div class="list-item">
						<div class="list-label">身份证号</div>
						<div class="list-control">
							<input type="text" placeholder="请输入15或18位身份证号" name="certNo" id="certNo"/>
						</div>
					</div>
					<div class="list-item list-custom">
						<div class="list-custom-top">
							<div class="list-label">手持身份证免冠照</div>
							<a class="list-label text-right font-weight-normal" id='showPhoto' href='javascript:;'>照片示例</a>
						</div>
						<div class="list-image">
							<div class="list-image-item-box">
								<div class="list-image-item">
									<div class="a-upload" id='img1'>
										<input type="file" name="certImg1" accept="image/*" onchange="fileUpload(this)"/>
										<div class="a-upload-tips"><span class="tips-add">+</span><span>正面</span></div>
									</div>
								</div>
							</div>
							<div class="list-image-item-box">
								<div class="list-image-item">
									<div class="a-upload" id='img2'>
										<input type="file" name="certImg2" accept="image/*" onchange="fileUpload(this)"/>
										<div class="a-upload-tips"><span class="tips-add">+</span><span>反面</span></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="list form">
				<div class="list-header">联系方式</div>
				<div class="list-body">
					<div class="list-item">
						<div class="list-label">手机号</div>
						<div class="list-control">
							<input type="text" placeholder="请输入手机号" name="contactMobile" id="contactMobile">
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">微信号</div>
						<div class="list-control">
							<input type="text" placeholder="非必填" name="contactWeixin" id="contactWeixin">
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">QQ号</div>
						<div class="list-control">
							<input type="text" placeholder="非必填" name="contactQq" id="contactQq">
						</div>
					</div>
				</div>
			</div>
			<div class="button-box">
				<div class="button" id="next">下一步，填写个人介绍</div>
			</div>
			<div class="footer-tips"><span class="footer-tips-span">为提高审核的成功率，请确保以上信息的真实性</span></div>
		</div>
		<div id="personalInfo" style="display: none;">
			<div class="list form">
				<div class="list-body">
					<div class="list-item">
						<div class="list-label">所学门派</div>
						<div class="list-control">
							<textarea class="list-textarea" rows="4" id="school" name='school' placeholder="非必填"></textarea>
							<div class="list-textarea-tips">
								还可填写200字
							</div>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">相关经历</div>
						<div class="list-control">
							<textarea class="list-textarea" rows="4" id="experience" name='experience' placeholder="非必填"></textarea>
							<div class="list-textarea-tips">
								还可填写200字
							</div>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">所获成就</div>
						<div class="list-control">
							<textarea class="list-textarea" rows="4" id="achievement" name='achievement' placeholder="非必填"></textarea>
							<div class="list-textarea-tips">
								还可填写200字
							</div>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">擅长领域</div>
						<div class="list-control">
							<textarea class="list-textarea" rows="4" id="goodAt" name='goodAt' placeholder="非必填"></textarea>
							<div class="list-textarea-tips">
								还可填写200字
							</div>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">目前职业</div>
						<div class="list-control">
							<input type="text" placeholder="非必填" id="profession" name='profession'>
						</div>
					</div>
					<div class="list-item">
						<div class="list-label">已签约其他风水平台</div>
						<div class="list-control">
							<div class="radio" id='radio' data-checked='false'>
								<input type="radio" name='isSignOther1' class="reset-radio"/>
								<input type="text" name='isSignOther' id='isSignOther' style="display: none; " />
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="treaty" style="height:4rem;position:relative;width:100%;display:flex;align-items:center; padding: 1.5rem;">
				<div class="radio on" id='radioTreaty' style="left:1.5rem;right:initial;"></div>
				<div style="margin-left: 2.5rem; font-size: 1.4rem; color:#3f3f3f; ">我同意并接受<a href='${host.base}/html/treaty.html'>雷门易平台协议</a></div>
			</div>

			<div style="padding-top:50px">
				<div class="button forword" id='forword'>上一步</div>
				<div class="button" id="next2">完成并提交申请</div>
			</div>
		</div>
	</form>
</div>
<div class='photo-example'>
	<div class="photo-wrapper">
		<div class="header">请分别拍摄<br/>手持身份证的正反面照片</div>
		<div class="body">
			<div class="item">
				<div class="title">正面示例</div>
				<img src="${host.img}/images/card_1.jpg"/>
			</div>
			<div class="item">
				<div class="title">反面示例</div>
				<img src="${host.img}/images/card_2.jpg"/>
			</div>
		</div>
		<div class="footer">
			<div class="button">我知道了</div>
		</div>
	</div>
</div>
<script>
$(function(){
	$("#radioTreaty").on("click", function(){
		if($(this).hasClass('on')){
			$(this).removeClass('on');
			$("#next2").addClass('disabled');
		}else{
			$(this).addClass('on');
			$("#next2").removeClass('disabled');
		}
	})
})
</script>
</body>
</html>