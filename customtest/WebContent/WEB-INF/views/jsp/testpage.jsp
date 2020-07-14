<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>customTest</title>
</head>
<body>
	<H3>파일업로드 - form & jQuery-multifile 사용</H3>
	<form enctype="multipart/form-data" method="post" id = "uploadForm">
		<input type="file" name="fileNm" id="fileNm" class="afile3">
		<button id="uploadBtn">upload</button>
	</form>
	<div class = 'uploadDiv'>
		<div id="afile3-list" style="border:2px solid #c9c9c9;min-height:50px"></div>
	</div>
	<h2> DB에 저장된 파일 리스트 </h2>
	<div class = "downloadFileDiv">
		<ul id = "downloadList">
		</ul>
	</div>
	
	<hr>
	<form id="form1" name="form1" method="post" enctype="multipart/form-data">
	    <input type="file" id="fileInput" name="fileInput">
	    <button type="button" onclick="doExcelUploadProcess()">엑셀업로드 작업</button>
	    <button type="button" onclick="doExcelDownloadProcess()">엑셀다운로드 작업</button>
	</form>
	<div id="result">
	</div>
</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js" ></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/jquery-multifile@2.2.2/jquery.MultiFile.min.js"></script> <!-- jQuery MultiFile 2.2.2 -->
<script type="text/javascript" src="http://malsup.github.com/jquery.form.js"></script>

<script>
var cloneObj = $(".uploadDiv").clone();
var regex = new RegExp("(.*?)\.(exe|sh|alz)$"); // refuse file extansion
var maxSize = 10485760; //10 MB
var tempfileletgth = 0; // init
var tempList = "";
//stringbuffer
var StringBuilder = function(){
	this.buffer = new Array();
}
// Append string to asc
StringBuilder.prototype.Append = function(strValue){
	this.buffer[this.buffer.length] = strValue;
}
//return String
StringBuilder.prototype.ToString = function(){
	return this.buffer.join("");
}

var fileUpload = function f_fileUpload(){
    $("#uploadForm").ajaxForm({
        url : "/customtest/upload",
        dataType : "json",
		cache: false,
		processData: false,
		contentType: false,
        error : function(){
            alert("error!") ;
        },
        success : function(result){
			console.log("test/uploadAjaxAction success")
			console.log("upload Count :: " + result);
			if (result > 0){
				$(".uploadDiv").html(cloneObj.html());
				$('#uploadList').empty(); 
				tempfileletgth = 0;
				tempList = "";
				fileList();
	            alert("success") ;
			} else {
				alert("??? error?") ;
			}
        }
    });
};

var fileList = function f_fileList(){
	$.ajax({
		 url: '/customtest/fileList',
		 dataType: 'json',
		 contentType: 'application/json',
		 cache : false,
		 success: function(result){
			 console.log("/fileList")
			 console.log(result);
			 var sb = new StringBuilder();
			 for (i = 0 ; i < result.length ; i++){
				 var path = "";
				 path += result[i].uploadPath+ "\\" + result[i].uuid;
				 sb.Append("<li id='uploadFile' data-path='" + path + "'>"+result[i].fileName+"&nbsp;&nbsp;<button class = 'download'>down</button>&nbsp;&nbsp;<button class = 'delete'>delete</button></li>");
			 }
			 $('#downloadList').html(sb.ToString()); 
			 clickbind();
		 }
	}); //$.ajax  
};
var downloadFile = function f_downloadFile(downloadFilePath){
	$.ajax({
		 url: '/customtest/downloadAction?fileName='+downloadFilePath,
		 type: 'GET',
		 success: function(result){
			 if ( result != null || result != undefined || result != ""){
			 	window.location = '/customtest/downloadAction?fileName='+downloadFilePath;
			 }
		 }
	}); //$.ajax  
};
var deleteFile = function f_deleteFile(StringPath){
	var postData = { "path": StringPath};
	$.ajax({
	 url: '/customtest/deleteFileAction',
	 type: 'POST',
	 data: postData,
	 dataType: 'json',
	 success: function(result){
		 console.log("result :: "+ result);
		 if(result ==1){
			 console.log("delete success");
			 fileList();
		 }
	 }
	 }); //$.ajax
};
var clickbind = function f_clickbind(){
	$('#uploadBtn').unbind('click').click(function(event) {
		console.log("click upload button");
		fileUpload();
	});
	//download 
	$('.download').unbind('click').click(function(event){
		console.log("click download button");
		var pathLi = $(this).parent();
		var downloadFilePath = pathLi.data("path");
		downloadFilePath = encodeURIComponent(downloadFilePath);
		downloadFile(downloadFilePath);
	});
	//delete file
	$('.delete').unbind('click').click(function(event){
		console.log("click delete button");
		var deleteFileLi = $(this).parent();
		var StringPath = deleteFileLi.data("path");
		deleteFile(StringPath);
	});
};
var eventbind = function f_eventbind(){
	$(".uploadFile").change(function(e){
		console.log("eventbind - change file");
		changeFile();
	});
};

var doExcelUploadProcess = function f_doExcelUploadProcess(){
    var f = new FormData(document.getElementById('form1'));
    $.ajax({
        url: "uploadExcelFile",
        data: f,
        processData: false,
        contentType: false,
        type: "POST",
        success: function(data){
            console.log(data);
            document.getElementById('result').innerHTML = JSON.stringify(data);
        }
    })
}

var doExcelDownloadProcess = function f_doExcelDownloadProcess(){
    var f = document.form1;
    f.action = "downloadExcelFile";
    f.submit();
}

$(document).ready(function(){
    $('input.afile3').MultiFile({
        max: 10, //업로드 최대 파일 갯수 (지정하지 않으면 무한대)
//      accept: 'jpg|png|gif', //허용할 확장자(지정하지 않으면 모든 확장자 허용)
        maxfile: 10240, //각 파일 최대 업로드 크기
        maxsize: 20480,  //전체 파일 최대 업로드 크기
        STRING: { //Multi-lingual support : 메시지 수정 가능
            remove : "clear", //추가한 파일 제거 문구, 이미태그를 사용하면 이미지사용가능
            duplicate : "$file 은 이미 선택된 파일입니다.", 
            denied : "$ext 는(은) 업로드 할수 없는 파일확장자입니다.",
             selected:'$file 을 선택했습니다.', 
            toomuch: "업로드할 수 있는 최대크기를 초과하였습니다.($size)", 
            toomany: "업로드할 수 있는 최대 갯수는 $max개 입니다.",
            toobig: "$file 은 크기가 매우 큽니다. (max $size)"
        },
        list:"#afile3-list" //파일목록을 출력할 요소 지정가능
    });
	fileList();
	eventbind();
	// branch Test add code
	conflict test ?
	
	askjdhvakjsdhfkljh
})
</script>
</html>