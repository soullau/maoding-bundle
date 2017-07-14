/*TMODJS:{"version":"1.0.0"}*/
!function () {

    function template (filename, content) {
        return (
            /string|function/.test(typeof content)
            ? compile : renderFile
        )(filename, content);
    };


    var cache = template.cache = {};
    var String = this.String;

    function toString (value, type) {

        if (typeof value !== 'string') {

            type = typeof value;
            if (type === 'number') {
                value += '';
            } else if (type === 'function') {
                value = toString(value.call(value));
            } else {
                value = '';
            }
        }

        return value;

    };


    var escapeMap = {
        "<": "&#60;",
        ">": "&#62;",
        '"': "&#34;",
        "'": "&#39;",
        "&": "&#38;"
    };


    function escapeFn (s) {
        return escapeMap[s];
    }


    function escapeHTML (content) {
        return toString(content)
        .replace(/&(?![\w#]+;)|[<>"']/g, escapeFn);
    };


    var isArray = Array.isArray || function(obj) {
        return ({}).toString.call(obj) === '[object Array]';
    };


    function each (data, callback) {
        if (isArray(data)) {
            for (var i = 0, len = data.length; i < len; i++) {
                callback.call(data, data[i], i, data);
            }
        } else {
            for (i in data) {
                callback.call(data, data[i], i);
            }
        }
    };


    function resolve (from, to) {
        var DOUBLE_DOT_RE = /(\/)[^/]+\1\.\.\1/;
        var dirname = ('./' + from).replace(/[^/]+$/, "");
        var filename = dirname + to;
        filename = filename.replace(/\/\.\//g, "/");
        while (filename.match(DOUBLE_DOT_RE)) {
            filename = filename.replace(DOUBLE_DOT_RE, "/");
        }
        return filename;
    };


    var utils = template.utils = {

        $helpers: {},

        $include: function (filename, data, from) {
            filename = resolve(from, filename);
            return renderFile(filename, data);
        },

        $string: toString,

        $escape: escapeHTML,

        $each: each
        
    };


    var helpers = template.helpers = utils.$helpers;


    function renderFile (filename, data) {
        var fn = template.get(filename) || showDebugInfo({
            filename: filename,
            name: 'Render Error',
            message: 'Template not found'
        });
        return data ? fn(data) : fn; 
    };


    function compile (filename, fn) {

        if (typeof fn === 'string') {
            var string = fn;
            fn = function () {
                return new String(string);
            };
        }

        var render = cache[filename] = function (data) {
            try {
                return new fn(data, filename) + '';
            } catch (e) {
                return showDebugInfo(e)();
            }
        };

        render.prototype = fn.prototype = utils;
        render.toString = function () {
            return fn + '';
        };

        return render;
    };


    function showDebugInfo (e) {

        var type = "{Template Error}";
        var message = e.stack || '';

        if (message) {
            // 利用报错堆栈信息
            message = message.split('\n').slice(0,2).join('\n');
        } else {
            // 调试版本，直接给出模板语句行
            for (var name in e) {
                message += "<" + name + ">\n" + e[name] + "\n\n";
            }  
        }

        return function () {
            if (typeof console === "object") {
                console.error(type + "\n\n" + message);
            }
            return type;
        };
    };


    template.get = function (filename) {
        return cache[filename.replace(/^\.\//, '')];
    };


    template.helper = function (name, helper) {
        helpers[name] = helper;
    };


    if (typeof define === 'function') {define(function() {return template;});} else if (typeof exports !== 'undefined') {module.exports = template;} else {this.template = template;}
    /*自动组合rootPath生成完整URL*/
template.helper('_url', function (url) {
    return window.rootPath + url;
});

/*判断字符串是否为undefined、Null或空*/
template.helper('_isNullOrBlank', function (str) {
    return str === void 0 || str === null || _.isBlank(str);
});

template.helper('_formatFileSize', function (fileSize) {
    var temp;
    if (fileSize === void 0 || fileSize === null)
        return '';
    else if (fileSize < 1024) {
        return fileSize + 'B';
    } else if (fileSize < (1024 * 1024)) {
        temp = parseFloat(fileSize / 1024).toFixed(3);
        return temp.substring(0, temp.length - 1) + 'KB';
    } else if (fileSize < (1024 * 1024 * 1024)) {
        temp = parseFloat(fileSize / (1024 * 1024)).toFixed(3);
        return temp.substring(0, temp.length - 1) + 'MB';
    } else {
        temp = parseFloat(fileSize / (1024 * 1024 * 1024)).toFixed(3);
        return temp.substring(0, temp.length - 1) + 'GB';
    }
});
    /*v:1*/
template('m_common/m_popover',function($data,$filename
/**/) {
'use strict';var $utils=this,$helpers=$utils.$helpers,$escape=$utils.$escape,popoverStyle=$data.popoverStyle,$string=$utils.$string,titleHtml=$data.titleHtml,contentStyle=$data.contentStyle,content=$data.content,$out='';$out+='<div class="popover m-popover box-shadow" role="tooltip" style="';
$out+=$escape(popoverStyle);
$out+='"> <div class="arrow" style="left: 50%;"></div>  ';
$out+=$string(titleHtml);
$out+=' <div class="popover-content" style="';
$out+=$escape(contentStyle);
$out+='"> ';
$out+=$string(content);
$out+=' </div> </div>';
return new String($out);
});/*v:1*/
template('m_common/m_popover_confirm',function($data,$filename
/**/) {
'use strict';var $utils=this,$helpers=$utils.$helpers,$string=$utils.$string,confirmMsg=$data.confirmMsg,$out='';$out+='<div> <p class="f-s-13">';
$out+=$string(confirmMsg);
$out+='</p> <p class="pull-right" > <button type="button" class="popover-btn-no btn btn-default btn-xs m-popover-close" style="line-height:22px;">取消 </button> <button type="button" class="popover-btn-yes btn btn-success btn-xs m-popover-submit" style="line-height:22px;">确定</button> </p> </div>';
return new String($out);
});/*v:1*/
template('m_common/m_sidebar',function($data,$filename
/**/) {
'use strict';var $utils=this,$helpers=$utils.$helpers,$escape=$utils.$escape,_url=$helpers._url,$out='';$out+='  <div class="page-sidebar navbar-collapse collapse">        <ul class="page-sidebar-menu" data-keep-expanded="false" data-auto-scroll="true" data-slide-speed="200">  <li class="heading"> <h3 class="uppercase">企业认证</h3> </li> <li class="nav-item"> <a href="';
$out+=$escape(_url('/orgAuth/approveList'));
$out+='" class="nav-link" data-nav-id="orgAuth-approveList"> <i class="icon-briefcase"></i> <span class="title">认证审核</span> <span class="selected"></span> </a> </li> <li class="heading"> <h3 class="uppercase">项目协同</h3> </li> <li class="nav-item"> <a href="';
$out+=$escape(_url('/corp/approveList'));
$out+='" class="nav-link" data-nav-id="corp-approveList"> <i class="icon-briefcase"></i> <span class="title">开通审核</span> </a> </li> </ul> </div>';
return new String($out);
});/*v:1*/
template('m_orgAuth/m_approveList','<div class="page-content" style="padding-top: 0;">  <div class="row"> <div class="col-md-12"> <div class="portlet light" style="overflow: hidden;"> <div class="portlet-title"> <div class="caption"> <i class="icon-paper-plane font-yellow-casablanca"></i> <span class="caption-subject bold font-yellow-casablanca uppercase">企业认证审核</span> </div> <div class="actions"> <a class="btn btn-circle btn-icon-only btn-default fullscreen" href="javascript:void(0);"> </a> </div> <div class="inputs" style="margin-right: 10px;"> <div class="portlet-input input-inline input-medium"> <div class="input-group"> <input type="text" class="form-control input-circle-left search-input" placeholder="请输入关键字"> <span class="input-group-btn"> <button class="btn btn-circle-right btn-default" data-action="search">搜索</button> </span> </div> </div> </div> </div> <div class="portlet-body"> <table class="table table-striped table-bordered table-hover table-checkable order-column" id="list"> <thead> <tr> <th class="text-center">序号</th> <th class="text-center">组织名称</th> <th class="text-center">企业名称</th> <th class="text-center">证件类型</th> <th class="text-center">注册号/统一社会代码</th> <th class="text-center">法人</th> <th class="text-center">经办人</th> <th class="text-center">认证时间</th> <th class="text-center">审核</th> <th class="text-center">审核人</th> <th class="text-center">审核时间</th> </tr> </thead> <tbody class="m-page-data"> </tbody> </table> <div class="clearfix"></div> <div class="m-page pull-right"></div> </div> </div> </div> </div> </div>');/*v:1*/
template('m_orgAuth/m_approveList_row',function($data,$filename
/**/) {
'use strict';var $utils=this,$helpers=$utils.$helpers,$each=$utils.$each,list=$data.list,o=$data.o,i=$data.i,$escape=$utils.$escape,pageIndex=$data.pageIndex,pageSize=$data.pageSize,_isNullOrBlank=$helpers._isNullOrBlank,$out='';$each(list,function(o,i){
$out+=' <tr class="odd gradeX"> <td>';
$out+=$escape(pageIndex*pageSize+i+1);
$out+='</td> <td>';
$out+=$escape(o.orgAlias);
$out+='</td> <td>';
$out+=$escape(o.orgName);
$out+='</td> <td class="text-center"> ';
if(o.businessLicenseType===0){
$out+=' 普通营业执照 ';
}else if(o.businessLicenseType===1){
$out+=' 多证合一营业执照 ';
}
$out+=' ';
if(!_isNullOrBlank(o.legalRepresentativePhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.legalRepresentativePhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td class="text-center">';
$out+=$escape(o.businessLicenseNumber);
$out+='</td> <td class="text-center"> ';
$out+=$escape(o.legalRepresentative);
$out+=' ';
if(!_isNullOrBlank(o.businessLicensePhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.businessLicensePhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td class="text-center"> ';
$out+=$escape(o.operatorName);
$out+=' ';
if(!_isNullOrBlank(o.operatorPhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.operatorPhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td class="text-center"> ';
$out+=$escape(o.applyDate);
$out+=' ';
if(!_isNullOrBlank(o.sealPhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.sealPhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td style="width: 90px;max-width: 90px;padding-left:15px;padding-right: 15px;"> ';
if(o.authenticationStatus === 0){
$out+=' <span class="label label-sm label-warning span-tag-60-25" style="width: 60px;">未提交</span> ';
}else if(o.authenticationStatus === 1){
$out+=' <div class="btn-toolbar"> <div class="btn-group"> <button class="btn btn-xs blue dropdown-toggle" type="button" data-toggle="dropdown" aria-expanded="false" style="width: 60px;"> 审核<i class="fa fa-angle-down"></i> </button> <ul class="dropdown-menu dropdown-menu-left dropdown-menu-v1" role="menu"> <li> <a href="javascript:void(0);" data-action="audit_pass" data-id="';
$out+=$escape(o.id);
$out+='">通过</a> </li> <li> <a href="javascript:void(0);" data-action="audit_reject" data-id="';
$out+=$escape(o.id);
$out+='">不通过</a> </li> </ul> </div> </div> ';
}else if(o.authenticationStatus === 2){
$out+=' <span class="label label-sm label-success span-tag-60-25">已通过</span> ';
}else if(o.authenticationStatus === 3){
$out+=' <span class="label label-sm label-danger span-tag-60-25">不通过</span> ';
}
$out+=' </td> <td class="text-center">';
$out+=$escape(o.auditorName);
$out+='</td> <td class="text-center">';
$out+=$escape(o.auditDate);
$out+='</td> </tr> ';
});
return new String($out);
});

}()