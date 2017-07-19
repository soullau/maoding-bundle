/* Write here your custom javascript codes */

/*阻止默认事件*/
var preventDefault = function (event) {
    if (event.preventDefault) {
        event.preventDefault();
    } else {
        event.returnValue = false;
    }
};

/*阻止冒泡*/
var stopPropagation = function (event) {
    if (event.stopPropagation) {
        event.stopPropagation();
    } else {
        event.cancelBubble = true;
    }
};

/*短格式时间
 * 1：00
 * 2：00
 * */
var shortTime = function (datetime) {
    return moment(datetime).format("HH:mm");
};


/*格式化日期
 * 今天 1：00
 * 昨天 2：00
 * 2017-01-01 2：00
 * */
var dateSpecFormat = function (datetime, pattern) {
    var now = moment(new Date(), 'YYYY-MM-DD HH:mm:ss');
    var yesterday = moment(new Date(), 'YYYY-MM-DD HH:mm:ss').subtract(1, 'days');
    var d = moment(moment(datetime).toDate(), 'YYYY-MM-DD HH:mm:ss');

    var nowFormat = now.format('YYYY-MM-DD');
    var yesterdayFormat = yesterday.format('YYYY-MM-DD');
    var dFormat = d.format('YYYY-MM-DD');

    var t1 = '';
    if (nowFormat == dFormat)
        t1 = '今天';
    else if (yesterdayFormat == dFormat)
        t1 = '昨天';
    else
        t1 = dFormat;

    if (pattern && !_.isBlank(pattern))
        return _.sprintf(pattern, t1, d.format('HH:mm'));

    return _.sprintf('%s %s', t1, d.format('HH:mm'));
};

/*格式化日期
 * 今天
 * 昨天
 * 2017-01-01
 * */
var dateSpecShortFormat = function (datetime) {
    var now = moment(new Date(), 'YYYY-MM-DD HH:mm:ss');
    var yesterday = moment(new Date(), 'YYYY-MM-DD HH:mm:ss').subtract(1, 'days');
    var d = moment(moment(datetime).toDate(), 'YYYY-MM-DD HH:mm:ss');

    var nowFormat = now.format('YYYY-MM-DD');
    var yesterdayFormat = yesterday.format('YYYY-MM-DD');
    var dFormat = d.format('YYYY-MM-DD');

    var t1 = '';
    if (nowFormat == dFormat)
        t1 = '今天';
    else if (yesterdayFormat == dFormat)
        t1 = '昨天';
    else
        t1 = dFormat;

    return t1;
};

/*判断字符串是否为undefined、Null或空*/
var isNullOrBlank = function (str) {
    return str === void 0 || str === null || _.isBlank(str);
};

//处理IE的Console.log兼容问题
(function () {
    var method;
    var noop = function () {
    };
    var methods = [
        'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error', 'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log', 'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd', 'timeStamp', 'trace', 'warn'
    ];
    var length = methods.length;
    var console = (window.console = window.console || {});
    while (length--) {
        method = methods[length];
        if (!console[method]) {
            console[method] = noop;
        }
    }
}());
//form数据提交
$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};
/**
 * 替换一段话中相匹配的字符串
 * g是全局m是多行
 * @param s1 处理的字符串
 * @param s2 替换的字符串
 * @returns
 */
String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
};

/**
 * 两日期对比
 * @param d1 时间一
 * @param d2 时间二
 * @returns {Number}
 */
function dateDiff(d1, d2) {
    if (!d1 || d1 == null || d1 == '' || !d2 || d2 == null || d2 == '') {
        return 0;
    }
    var result = Date.parse(d1.toString().replace(/-/g, "/")) - Date.parse(d2.toString().replace(/-/g, "/"));
    return result;
}
//获取当前日期
function getNowDate() {
    var date = new Date();
    var year = date.getFullYear(),
        mon = date.getMonth() < 9 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1,
        day = date.getDate() < 10 ? '0' + date.getDate() : date.getDate(),
        nowDate = year + "-" + mon + "-" + day;
    return nowDate;
}
/**
 * 时间差
 * @param stime
 * @param etime
 */
function diffDays(stime, etime) {
    var d1 = this.dateDiff(etime, stime);
    var day1 = Math.floor(d1 / (24 * 3600 * 1000));
    return day1;
}

/**
 * 获取字符串长度，中文按2个字节，英文按1个字节
 */
function getStringLength(str) {
    var len = 0;
    for (var i = 0; i < str.length; i++) {
        var c = str.charCodeAt(i);
        //单字节加1
        if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
            len++;
        }
        else {
            len += 2;
        }
    }
    return len;
}

/**
 * 获取字符串长度，中文按2个字节，英文按1个字节
 */
var cutString = function (str, length, suffix) {
    if (isNullOrBlank(str))
        return '';

    var len = 0;
    var temp = '';
    for (var i = 0; i < str.length; i++) {
        var c = str.charCodeAt(i);
        //单字节加1
        if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
            len++;
        }
        else {
            len += 2;
        }
        temp += str.charAt(i);
        if (len >= length)
            return temp + suffix;
    }
    return str;
};

/*****************************验证公共方法--结束**********************************/

//数据提交访问错误
function handlePostJsonError(res) {
    if (res.status == 404) {
        //当前请求地址未找到
        S_toastr.error('当前请求地址未找到！');

    } else if (res.status == 0) {
        //网络请求超时
        S_toastr.error('网络请求超时！');
    } else {
        S_toastr.error('网络请求出现错误！status：' + res.status + "，statusText：" + res.statusText);
    }
}

//数据提交访问错误
function handleResponse(res) {
    var result = false;
    if (res.code === '401') {
        //session超时 !
        S_toastr.error('当前用户状态信息已超时!点击“确定”后返回登录界面。', '提示', function () {
            window.location.href = rootPath + '/iWork/sys/login';
        });
        result = true;
    } else if (res.code === '500') {
        //未捕获异常 X
        S_toastr.error('出现异常错误 !详细信息：' + res.msg);
        result = true;
    }
    return result;
}

var S_layer = {
    dialog: function (options) {
        var defaults = {
            id: '',
            type: 1, /*0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）*/
            title: ' ', /*['文本', 'font-size:18px;'],false*/
            anim: -1, /*弹出动画，0-6*/
            isOutAnim: false, /*关闭动画*/
            time: 0, /*自动关闭所需毫秒*/
            area: 'auto', /*['390px', '330px'],auto*/
            /*maxWidth: '360px', *//*只有当area: 'auto'时，maxWidth的设定才有效。*/
            shade: 0.3,
            shadeClose: false, /*是否点击遮罩关闭*/
            closeBtn: 1, /*关闭按钮 0-2*/
            move: '.layui-layer-title', /*触发拖动的元素,false*/
            moveOut: true, /*是否允许拖拽到窗口外*/
            maxmin: true,
            moveEnd: null, /*function(layero){}*/
            fixed: true, /*鼠标滚动时，层是否固定在可视区域*/
            /*offset: [($(window).height() - 300)/2, ($(window).width() - 390)/2],*/
            content: null,
            btn: ['确定', '取消'],
            btnAlign: 'r', /*按钮位置l-c-r*/
            yes: function (index, layero) {
                layer.close(index);
            },
            cancel: function (index, layero) {
            },
            btn2: function (index, layero) {
                layer.close(index);
                //layer.closeAll();
                //return false 开启该代码可禁止点击该按钮关闭
            },
            resize: false,
            resizing: null, /*function(layero){}*/
            scrollbar: false,
            zIndex: layer.zIndex,
            success: function (layero) {
                layer.setTop(layero);
            },
            full: null, /*function(layero){}*/
            min: null, /*function(layero){}*/
            restore: null, /*function(layero){}*/
            end: null /*function(){}*/
        };
        var opts = $.extend(true, {}, defaults, options);
        layer.open(opts);
    },
    panel: function (options) {
        var defaults = {
            id: '',
            type: 1, /*0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）*/
            title: false, /*['文本', 'font-size:18px;'],false*/
            anim: -1, /*弹出动画，0-6*/
            isOutAnim: false, /*关闭动画*/
            time: 0, /*自动关闭所需毫秒*/
            area: 'auto', /*['390px', '330px'],auto*/
            /*maxWidth: '360px', *//*只有当area: 'auto'时，maxWidth的设定才有效。*/
            shade: 0.3,
            shadeClose: false, /*是否点击遮罩关闭*/
            closeBtn: 0, /*关闭按钮 0-2*/
            move: '.layui-layer-title', /*触发拖动的元素,false*/
            moveOut: true, /*是否允许拖拽到窗口外*/
            maxmin: false,
            moveEnd: null, /*function(layero){}*/
            fixed: true, /*鼠标滚动时，层是否固定在可视区域*/
            /*offset: [($(window).height() - 300)/2, ($(window).width() - 390)/2],*/
            content: null,
            btn: null,
            btnAlign: 'r', /*按钮位置l-c-r*/
            cancel: function (index, layero) {
            },
            shown: null, /*function(layero){}*/
            resize: false,
            resizing: null, /*function(layero){}*/
            scrollbar: false,
            zIndex: layer.zIndex,
            full: null, /*function(layero){}*/
            min: null, /*function(layero){}*/
            restore: null, /*function(layero){}*/
            end: null /*function(){}*/
        };
        var opts = $.extend(true, {}, defaults, options);
        opts.type = 1;
        opts.title = false;
        opts.move = '.portlet-title';
        opts.btn = null;
        opts.maxmin = null;
        opts.closeBtn = 0;
        opts.success = function (layero) {
            layer.setTop(layero);

            var $btnYes = layero.find('button[data-action="layer-custom-btn-yes"]');
            if ($btnYes && $btnYes.length > 0) {
                $btnYes.off('click.yes').on('click.yes', function () {
                    if (opts.yes !== void 0 && opts.yes !== null)
                        opts.yes(layer.index, layero);
                });
            }

            var $btnClose = layero.find('button[data-action="layer-custom-btn-close"]');
            if ($btnClose && $btnClose.length > 0) {
                $btnClose.off('click.close').on('click.close', function () {
                    layer.close(layer.index);
                });
            }

            if (opts.shown !== void 0 && opts.shown !== null)
                opts.shown(layer.index, layero);
        };
        layer.open(opts);
    }
};

var S_swal = {
    confirm: function (options, callback) {
        var defaults = {
            title: '您确定要进行该操作吗？',
            text: '',
            type: 'info',
            allowOutsideClick: true,
            showConfirmButton: true,
            showCancelButton: true,
            confirmButtonClass: 'btn-success',
            cancelButtonClass: 'btn-danger',
            closeOnConfirm: true,
            closeOnCancel: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消'
        };
        var opts = $.extend(true, {}, defaults, options);
        swal(opts, callback);
    }
};

var S_toastr = {
    success: function (text) {
        toastr.options = {
            "closeButton": false,
            "debug": false,
            "progressBar": false,
            "preventDuplicates": false,
            "positionClass": "toast-top-center",
            "onclick": null,
            "showDuration": "400",
            "hideDuration": "1000",
            "timeOut": "3000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        toastr.success(text);
    },
    warning: function (text) {
        toastr.options = {
            "closeButton": false,
            "debug": false,
            "progressBar": false,
            "preventDuplicates": false,
            "positionClass": "toast-top-center",
            "onclick": null,
            "showDuration": "400",
            "hideDuration": "1000",
            "timeOut": "3000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        toastr.warning(text);
    },
    info: function (text) {
        toastr.options = {
            "closeButton": false,
            "debug": false,
            "progressBar": false,
            "preventDuplicates": false,
            "positionClass": "toast-top-center",
            "onclick": null,
            "showDuration": "400",
            "hideDuration": "1000",
            "timeOut": "3000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        toastr.info(text);
    },
    error: function (text) {
        toastr.options = {
            "closeButton": false,
            "debug": false,
            "progressBar": false,
            "preventDuplicates": false,
            "positionClass": "toast-top-center",
            "onclick": null,
            "showDuration": "400",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        toastr.error(text);
    }
};

/*var S_swal = {
 confirm: function (option) {
 swal({
 title: option.title || "确定此操作吗?",
 text: option.text,
 type: "warning",
 showCancelButton: true,
 confirmButtonColor: "#DD6B55",
 confirmButtonText: option.confirmButtonText || "确定",
 cancelButtonText: option.cancelButtonText || "取消",
 closeOnConfirm: option.closeOnConfirm || false
 }, function () {
 if (option.callBack != null) {
 option.callBack();
 }
 });
 },
 sure: function (option) {
 swal({
 title: option.title || "确定此操作吗?",
 text: option.text,
 type: "success",
 confirmButtonText: option.confirmButtonText || "确定",
 closeOnConfirm: true
 }, function () {
 if (option.callBack != null) {
 option.callBack();
 }
 });
 }
 };*/

/******************************************** 弹窗方法 结束 *****************************************************/


var m_ajax = {
    getJson: function (options, onHttpSuccess, onHttpError) {
        $.ajax({
            type: 'GET',
            url: options.url,
            cache: false,
            contentType: "application/json",
            beforeSend: function () {
                if (options.loadingEl)
                {
                    App.blockUI({
                        target: options.loadingEl,
                        boxed: false,
                        animate: true
                    });
                }

                if (options.bindDisabled) {
                    var $el = $(options.bindDisabled);
                    if ($el.length > 0) {
                        try {
                            $el.attr('disabled', true);
                        } catch (e) {
                        }
                        try {
                            $el.prop('disabled', true);
                        } catch (e) {
                        }
                    }
                }
            },
            success: function (response) {

                if (!handleResponse(response)) {
                    if (onHttpSuccess)
                        onHttpSuccess(response);
                }

            },
            error: function (response) {
                if (onHttpError)
                    onHttpError();

                handlePostJsonError(response);
                //else
                //tzTips.showOnTopRight("Ajax请求发生错误", "error");
            },
            complete: function () {
                if (options.loadingEl)
                    App.unblockUI();

                if (options.bindDisabled) {
                    setTimeout(function () {
                        var $el = $(options.bindDisabled);
                        if ($el.length > 0) {
                            try {
                                $el.attr('disabled', false);
                            } catch (e) {
                            }
                            try {
                                $el.prop('disabled', false);
                            } catch (e) {
                            }
                        }
                    }, 1000);

                }
            }
        });
    },
    postJson: function (options, onHttpSuccess, onHttpError) {
        $.ajax({
            type: 'POST',
            url: options.url,
            cache: false,
            async: options.async == null ? true : options.async,
            data: JSON.stringify(options.postData),
            contentType: "application/json",
            beforeSend: function () {
                if (options.loadingEl)
                {
                    App.blockUI({
                        target: options.loadingEl,
                        boxed: false,
                        animate: true
                    });
                }

                if (options.bindDisabled) {
                    var $el = $(options.bindDisabled);
                    if ($el.length > 0) {
                        try {
                            $el.attr('disabled', true);
                        } catch (e) {
                        }
                        try {
                            $el.prop('disabled', true);
                        } catch (e) {
                        }
                    }
                }
            },
            success: function (response) {
                if (!handleResponse(response)) {
                    if (onHttpSuccess)
                        onHttpSuccess(response);
                }
            },
            error: function (response) {
                if (onHttpError)
                    onHttpError();

                handlePostJsonError(response);
                //else
                //tzTips.showOnTopRight("Ajax请求发生错误", "error");
            },
            complete: function () {
                if (options.loadingEl)
                    App.unblockUI();

                if (options.bindDisabled) {
                    setTimeout(function () {
                        var $el = $(options.bindDisabled);
                        if ($el.length > 0) {
                            try {
                                $el.attr('disabled', false);
                            } catch (e) {
                            }
                            try {
                                $el.prop('disabled', false);
                            } catch (e) {
                            }
                        }
                    }, 1000);
                }
            }
        });
    }
};

//num表示要四舍五入的数,v表示要保留的小数位数。
function decimal(num, v) {
    var vv = Math.pow(10, v);
    return Math.round(num * vv) / vv;
}

//计算应显示的小数位
var countDigits = function (val, maxDigits) {
    var splits = (val - 0).toString().split(".");
    var digits = 0;
    if (splits.length > 1) {
        if (splits[1].length < maxDigits)
            digits = splits[1].length;
        else
            digits = maxDigits;
    }
    return digits;
};

//精准计算
var doMath = {
    //精确减法
    accSub: function (a1, a2) {
        var r1, r2, m, n;
        try {
            r1 = a1.toString().split(".")[1].length
        } catch (e) {
            r1 = 0
        }
        try {
            r2 = a2.toString().split(".")[1].length
        } catch (e) {
            r2 = 0
        }
        m = Math.pow(10, Math.max(r1, r2));
        //动态控制精度长度
        n = (r1 >= r2) ? r1 : r2;
        return ((a1 * m - a2 * m) / m).toFixed(n);
    },
    //精确加法
    accAdd: function (a1, a2) {
        var r1, r2, m;
        try {
            r1 = a1.toString().split(".")[1].length
        } catch (e) {
            r1 = 0
        }
        try {
            r2 = a2.toString().split(".")[1].length
        } catch (e) {
            r2 = 0
        }
        m = Math.pow(10, Math.max(r1, r2));
        return (a1 * m + a2 * m) / m;
    }
};

//封装成Number类型的子方法，调用方法如: 8-4 写成 8.sub(4)
Number.prototype.sub = function (arg) {//减法
    return parseFloat(doMath.accSub(this, arg));
};
Number.prototype.add = function (arg) {//加法
    return parseFloat(doMath.accAdd(this, arg));
};


/**
 * Created by Wuwq on 2017/2/27.
 */
var restApi = {
    /** 登陆 **/
    url_loginSubmit: window.rootPath + '/account/login',

    /** 企业认证 **/
    url_getAuthenticationPage: window.rootPath + '/orgAuth/getAuthenticationPage',
    url_authorizeAuthentication: window.rootPath + '/orgAuth/authorizeAuthentication'
};
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


template.helper('_isNowDiffTargetDateNDaysMore', function (targetDate, n) {
    var diff = moment().diff(moment(targetDate), 'days') + 1;
    return diff >= n;
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
$out+='" class="nav-link" data-nav-id="orgAuth-approveList"> <i class="icon-briefcase"></i> <span class="title">认证审核</span> <span class="selected"></span> </a> </li>  </ul> </div>';
return new String($out);
});/*v:1*/
template('m_orgAuth/m_orgAuth_list','<div class="page-content" style="padding-top: 0;">  <div class="row"> <div class="col-md-12"> <div class="portlet light" style="overflow: hidden;"> <div class="portlet-title"> <div class="caption">  <span class="caption-subject bold font-yellow-casablanca uppercase">企业认证审核</span> </div> <div class="actions"> <a class="btn btn-circle btn-icon-only btn-default fullscreen" href="javascript:void(0);"> </a> </div> <div class="inputs" style="margin-right: 10px;"> <div class="portlet-input input-inline input-medium"> <div class="input-group"> <input type="text" class="form-control input-circle-left search-input" placeholder="请输入关键字"> <span class="input-group-btn"> <button class="btn btn-circle-right btn-default" data-action="search">搜索</button> </span> </div> </div> </div> </div> <div class="portlet-body"> <table class="table table-striped table-bordered table-hover" id="list"> <thead> <tr> <th>序号</th> <th>组织名称</th> <th>企业名称</th> <th>证件类型</th> <th>注册号/统一社会代码</th> <th>法人</th> <th>经办人</th> <th name="field_applyDate">认证时间</th> <th class="text-center">审核状态</th> <th>审核人</th> <th name="field_auditDate">审核时间</th> </tr> </thead> <tbody class="m-page-data"> </tbody> </table> </div> <div class="clearfix"></div> <div id="orgAuthList_sortSwap"></div> <div class="m-page pull-right" style="margin-bottom: 20px;"></div> </div> </div> </div> </div>');/*v:1*/
template('m_orgAuth/m_orgAuth_list_row',function($data,$filename
/**/) {
'use strict';var $utils=this,$helpers=$utils.$helpers,$each=$utils.$each,list=$data.list,o=$data.o,i=$data.i,$escape=$utils.$escape,pageIndex=$data.pageIndex,pageSize=$data.pageSize,_isNullOrBlank=$helpers._isNullOrBlank,_isNowDiffTargetDateNDaysMore=$helpers._isNowDiffTargetDateNDaysMore,$out='';$each(list,function(o,i){
$out+=' <tr class="odd gradeX"> <td>';
$out+=$escape(pageIndex*pageSize+i+1);
$out+='</td> <td>';
$out+=$escape(o.orgAlias);
$out+='</td> <td>';
$out+=$escape(o.orgName);
$out+='</td> <td> ';
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
$out+=' </td> <td>';
$out+=$escape(o.businessLicenseNumber);
$out+='</td> <td> ';
$out+=$escape(o.legalRepresentative);
$out+=' ';
if(!_isNullOrBlank(o.businessLicensePhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.businessLicensePhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td> ';
$out+=$escape(o.operatorName);
$out+=' ';
if(!_isNullOrBlank(o.operatorPhoto)){
$out+=' <a href="javascript:void(0);" data-action="preview" data-url="';
$out+=$escape(o.operatorPhoto);
$out+='"><i class="fa fa-file-image-o"></i></a> ';
}
$out+=' </td> <td> ';
if(o.authenticationStatus === 1 && _isNowDiffTargetDateNDaysMore(o.applyDate,3)){
$out+=' <span style="color: red;">';
$out+=$escape(o.applyDate);
$out+='</span> ';
}else{
$out+=' ';
$out+=$escape(o.applyDate);
$out+=' ';
}
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
$out+=' <div class="btn-toolbar"> <div class="btn-group"> <button class="btn btn-xs btn-info dropdown-toggle" type="button" data-toggle="dropdown" aria-expanded="false" style="width: 60px;"> 审核 </button> <ul class="dropdown-menu dropdown-menu-left dropdown-menu-v1" role="menu"> <li> <a href="javascript:void(0);" data-action="audit_pass" data-id="';
$out+=$escape(o.id);
$out+='">通过</a> </li> <li> <a href="javascript:void(0);" data-action="audit_reject" data-id="';
$out+=$escape(o.id);
$out+='">不通过</a> </li> </ul> </div> </div> ';
}else if(o.authenticationStatus === 2){
$out+='  <div class="btn-toolbar"> <div class="btn-group"> <button class="btn btn-xs btn-success dropdown-toggle" type="button" data-toggle="dropdown" aria-expanded="false" style="width: 60px;"> 已通过 </button> <ul class="dropdown-menu dropdown-menu-left dropdown-menu-v1" role="menu"> <li> <a href="javascript:void(0);" data-action="audit_reject" data-id="';
$out+=$escape(o.id);
$out+='">重置为不通过</a> </li> </ul> </div> </div> ';
}else if(o.authenticationStatus === 3){
$out+='  <div class="btn-toolbar"> <div class="btn-group"> <button class="btn btn-xs btn-danger dropdown-toggle" type="button" data-toggle="dropdown" aria-expanded="false" style="width: 60px;"> 不通过 </button> <ul class="dropdown-menu dropdown-menu-left dropdown-menu-v1" role="menu"> <li> <a href="javascript:void(0);" data-action="audit_view" data-id="';
$out+=$escape(o.id);
$out+='" data-reject-type="';
$out+=$escape(o.rejectType);
$out+='" data-reject-reason="';
$out+=$escape(o.rejectReason);
$out+='" data-auditor-name="';
$out+=$escape(o.auditorName);
$out+='">查看原因</a> </li> <li> <a href="javascript:void(0);" data-action="audit_pass" data-id="';
$out+=$escape(o.id);
$out+='">重置为通过</a> </li> </ul> </div> </div> ';
}
$out+=' </td> <td>';
$out+=$escape(o.auditorName);
$out+='</td> <td>';
$out+=$escape(o.auditDate);
$out+='</td> </tr> ';
});
return new String($out);
});/*v:1*/
template('m_orgAuth/m_orgAuth_reject','<div class="portlet light" style="overflow: hidden;"> <div class="portlet-title"> <div class="caption">  <span class="caption-subject bold font-yellow-casablanca uppercase">不通过原因</span> </div> <div class="tools"> <a href="" class="remove" data-original-title="" title=""> </a> </div> </div> <div class="portlet-body"> <form class="form-horizontal" role="form"> <div class="form-body"> <div class="form-group"> <label class="col-md-2 control-label">原因：</label> <div class="col-md-9"> <select id="rejectType"></select> </div> </div> <div class="form-group"> <label class="col-md-2 control-label">说明：</label> <div class="col-md-9"> <textarea id="rejectReason" class="form-control" rows="5"></textarea> </div> </div> </div> </form> </div> <div class="modal-footer"> <button type="button" class="btn btn-success" data-action="layer-custom-btn-yes">确&nbsp;定</button> <button type="button" class="btn btn-default" data-action="layer-custom-btn-close">取&nbsp;消</button> </div> </div>');/*v:1*/
template('m_orgAuth/m_orgAuth_view','<div class="portlet light" style="overflow: hidden;"> <div class="portlet-title"> <div class="caption">  <span class="caption-subject bold font-yellow-casablanca uppercase">不通过原因</span> </div> <div class="tools"> <a href="" class="remove" data-original-title="" title=""> </a> </div> </div> <div class="portlet-body"> <form class="form-horizontal" role="form"> <div class="form-body"> <div class="form-group"> <label class="col-md-2 control-label">原因：</label> <div class="col-md-9"> <select id="rejectType"></select> </div> </div> <div class="form-group"> <label class="col-md-2 control-label">说明：</label> <div class="col-md-9"> <textarea id="rejectReason" class="form-control" rows="5"></textarea> </div> </div> </div> </form> </div> <div class="modal-footer"> <button type="button" class="btn btn-default" data-action="layer-custom-btn-close">关&nbsp;闭</button> </div> </div>');

}()
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_popover",
        defaults = {
            clearOnInit: true,//初始化是否清掉其他的Popover
            titleHtml: null,
            hideArrow: false,
            popoverStyle: '',//父容器popover的追加样式，如弹窗宽度
            contentStyle: 'padding: 10px 14px 10px;',//popover-content的追加样式
            content: null,//自定义内容，可以用模板
            placement: null,//浮窗是在哪个位置展开：‘left’,‘right’,‘top’,‘bottom’,空值则默认为top
            onShown: null,//浮窗显示后的事件，可以用来重新绑定值
            onSave: null,//提交事件
            onClear: null,//清除事件
            onClose: null,//关闭事件
            template: 'm_common/m_popover',//主框架,
            closeOnDocumentClicked: null//自定义样式类处理关闭editable
        };

    // The actual plugin constructor
    function Plugin(element, options) {
        this.element = element;

        this.settings = options;
        this._defaults = defaults;
        this._name = pluginName;
        this._popoverTop = null;//初始化页面时，保存浮窗的top值
        this._popoverHeight = null;//初始化页面时，保存浮窗的height值
        this.init();
    }

    // Avoid Plugin.prototype conflicts
    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;

            if (that.settings.clearOnInit === true) {
                //清掉其他的Popover
                $(document).find('.m-popover').each(function (i, o) {
                    $(o).remove();
                });

                $(document).off('click.m-popover');
            }

            var html = template(that.settings.template, {
                titleHtml: that.settings.titleHtml,
                popoverStyle: that.settings.popoverStyle,
                contentStyle: that.settings.contentStyle,
                content: that.settings.content
            });

            $(html).insertAfter($(that.element));
            var $popover = $(that.element).next('.m-popover');

            if (that.settings.hideArrow === true)
                $popover.find('.arrow').eq(0).hide();

            if (that.settings.onShown && that.settings.onShown !== null)
                that.settings.onShown($popover);

            //防止冒泡
            $popover.off('click.m-popover').on('click.m-popover', function (e) {
                stopPropagation(e);
                return false;
            });

            setTimeout(function () {
                that.setPosition();
                that.bindBtnClick();
                that.bindPopoverClickedOut();
            }, 50);

            //绑定回车事件
            $popover.find('input[type="text"]').keydown(function () {
                if (event.keyCode == '13') {//keyCode=13是回车键
                    var $btnSubmit = $popover.find('.m-popover-submit');
                    if ($btnSubmit && $btnSubmit.length > 0)
                        $btnSubmit.click();
                }
            });

        },
        //当鼠标点击的焦点不在浮窗内时，关闭浮窗
        bindPopoverClickedOut: function () {
            var that = this;
            $(document).on('click.m-popover', function (e) {
                //console.log('document.clicked');
                var flag = $(e.target).parents('.select2-container').length > 0 || $(e.target).is('.select2-container');
                //防止select2搜索框点击触发关闭
                if ($(e.target).closest('.select2-search__field').length > 0 || flag)
                    return false;

                if (typeof that.settings.closeOnDocumentClicked === 'function') {
                    if (that.settings.closeOnDocumentClicked(e) === false)
                        return false;
                }

                if (that.settings.onClose && that.settings.onClose !== null) {
                    //返回false则不关闭
                    if (that.settings.onClose($(e.target)) !== false)
                        that.closeFilter();
                }
                else {
                    that.closeFilter();
                }
            });
        },
        setPosition: function () {
            var that = this;
            var $popover = $(that.element).next('.m-popover');
            if ($popover.length > 0) {
                var p_p = that.settings.placement ? that.settings.placement : 'top';//浮窗的展示位置
                var a_ptop = $(that.element).position().top;//a标签的top值
                var a_width = $(that.element).outerWidth();//a标签的width值
                var a_height = $(that.element).outerHeight();//a标签的height值
                var a_pleft = $(that.element).position().left;//a标签的left值
                var p_width = $popover.width();//浮窗的宽度
                var p_height = $popover.outerHeight();//浮窗的高度
                var p_top = 0;//浮窗的top值
                var p_left = 0;//浮窗的left值
                switch (p_p) {
                    case 'top':
                        p_top = (a_ptop - p_height);
                        p_left = a_pleft + a_width / 2 - p_width / 2;
                        break;
                    case 'bottom':
                        p_top = (a_ptop + a_height);
                        p_left = a_pleft + a_width / 2 - p_width / 2;
                        break;
                    case 'left':
                        p_top = (a_ptop - p_height / 2 + 5);
                        p_left = a_pleft - p_width - 10;
                        break;
                    case 'right':
                        p_top = (a_ptop - p_height / 2 + 7);
                        p_left = a_pleft + a_width;
                        break;


                };
                that._popoverTop = p_top;
                that._popoverHeight = p_height;
                $popover.removeClass('top').addClass(p_p);
                if (p_p.indexOf('left') > -1 || p_p.indexOf('right') > -1) {
                    $popover.find('.arrow').css({'top': '50%', 'left': ''});
                }

                $popover.css({
                    display: 'inline-block',
                    position: 'absolute',
                    top: p_top,
                    left: p_left
                });
            }
        },
        bindBtnClick: function () {
            var that = this;
            var $popover = $(that.element).next('.m-popover');
            if ($popover.length > 0) {

                //查找【提交按钮】并绑定事件
                var $btnSubmit = $popover.find('.m-popover-submit');
                if ($btnSubmit.length > 0) {
                    $btnSubmit.click(function (e) {

                        if (that.settings.onSave && that.settings.onSave !== null) {
                            if (that.settings.onSave($popover) !== false)
                                that.closeFilter();
                        }
                        else
                            that.closeFilter();

                        stopPropagation(e);
                        return false;
                    });
                }

                //查找【清除按钮】并绑定事件
                var $btnClear = $popover.find('.m-popover-clear');
                if ($btnClear.length > 0) {
                    $btnClear.click(function (e) {

                        //如果没有自定义清除函数，则使用默认
                        if (that.settings.onClear && that.settings.onClear !== null)
                            that.settings.onClear($popover);
                        else {
                            //查找第一个input清空
                            var $input = $(this).closest('form').find('input:first');
                            if ($input.length > 0)
                                $input.val('');
                        }

                        stopPropagation(e);
                        return false;
                    });
                }

                //查找【关闭按钮】并绑定事件
                var $btnClose = $popover.find('.m-popover-close');
                if ($btnClose.length > 0) {
                    $btnClose.click(function (e) {

                        if (that.settings.onClose && that.settings.onClose !== null) {
                            //返回false则不关闭
                            if (that.settings.onClose($popover) !== false)
                                that.closeFilter();
                        }
                        else {
                            that.closeFilter();
                        }

                        stopPropagation(e);
                        return false;
                    });
                }
                ;
                /*var $btnSubmit = $popover.find('.m-popover-submit');
                 //点击submit按钮或浮窗其他地方，出现验证信息时，相应改变popover的top值
                 $popover.find('.popover-content,button,input').off('click.changePosition').on('click.changePosition',function(e){
                 setTimeout(function(){
                 that.changePosition($popover);
                 },20);
                 });
                 //点击input表单，出现验证信息时，相应改变popover的top值
                 $popover.find('input[type="text"]').off('keyup.changePosition').on('keyup.changePosition',function(e){
                 setTimeout(function(){
                 console.log(2222)
                 that.changePosition($popover);
                 },20);
                 });*/
                $popover.resize(function () {
                    setTimeout(function () {
                        that.changePosition($popover);
                    }, 20);
                });
            }
        },
        //通过改变弹窗的top值来改变弹窗的位置
        changePosition: function ($popover) {
            var that = this;
            var errTag = $popover.find('label.error').length;
            var errTagH = errTag * ($popover.find('label.error').outerHeight() - 0);
            var popH = $popover.height();
            var popT = $popover.position().top;
            var h1 = popH - that._popoverHeight;

            if (that.settings.placement != null && that.settings.placement == 'top') {//当为top，popoverTop需要设置
                if (h1 == -4) {
                    $popover.css('top', that._popoverTop + 'px');
                } else {
                    var newTop = that._popoverTop - h1 - 4;
                    $popover.css('top', newTop);
                }

            } else if (that.settings.placement != null && (that.settings.placement == 'left' || that.settings.placement == 'right')) {

                $popover.find('.arrow').css('top', (that._popoverHeight) / 2 + 'px');
            }
        },
        closeFilter: function () {
            var that = this;
            $(that.element).siblings('.m-popover').each(function (i, o) {
                $(o).remove();
            });
            $(document).off('click.m-popover');
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);



/**
 * Created by Wuwq on 2017/1/5.
 */
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_sidebar",
        defaults = {};

    function Plugin(element, options) {
        this.element = element;

        this.settings = options;
        this._defaults = defaults;
        this._name = pluginName;
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            that._render();
        }
        , _render: function () {
            var that = this;
            var html = template('m_common/m_sidebar', {});
            $(that.element).html(html);
            that._bindAction();
        }
        , _bindAction:function () {
            var that=this;
            var navId=$(that.element).attr('data-nav-id');
            $(that.element).find('a.nav-link').each(function(i,o){
                var $el=$(o);
                if($el.attr('data-nav-id')===navId){
                    $el.closest('.nav-item').addClass("active open");
                }
            });
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);
/**
 * Created by Wuwq on 2017/1/5.
 */
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_orgAuth",
        defaults = {};

    function Plugin(element, options) {
        this.element = element;

        this.settings = options;
        this._defaults = defaults;
        this._name = pluginName;
        this._rejectReasons = [{id: 1, text: '企业认证名称与营业执照不符'},
            {id: 2, text: '营业执照复印件无盖红章'},
            {id: 3, text: '营业执照复印件无签署文字“仅用于卯丁认证使用'},
            {id: 4, text: '营业执照过期'},
            {id: 5, text: '营业执照图片模糊'},
            {id: 6, text: '法人身份证与营业执照不符合'},
            {id: 7, text: '法人身份证图片模糊'},
            {id: 8, text: '法人身份证已过期'},
            {id: 8, text: '经办人身份证图片模糊'},
            {id: 9, text: '经办人身份证已过期'},
            {id: 10, text: '证照无法查验'}];
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            that._render();
        },
        _render: function () {
            var that = this;
            var html = template('m_orgAuth/m_orgAuth_list', {});
            $(that.element).html(html);
            that._bindPage();
            that._bindSearch();
            that._bindSortFields();
        },
        _bindPage: function () {
            var that = this;
            var $page = $(that.element).find('.m-page:first');
            var remote = that._remote();
            $page.m_page({
                loadingEl: $(that.element).find('.portlet'),
                pageSize: 20,
                remote: remote
            }, true);
        },
        _remote: function () {
            var that = this;
            var $page = $(that.element).find('.m-page:first');
            var postData = function (data) {
                var postData = {pageIndex: data.pageIndex, pageSize: data.pageSize};
               /* $.extend(true, postData, {authenticationStatus: 2});*/
                var searchKey = $(that.element).find('.search-input').val();
                if (!isNullOrBlank(searchKey))
                    $.extend(true, postData, {orgNameMask: searchKey, orgAliasMask: searchKey});

                //排序
                var sortField = $('#orgAuthList_sortSwap').attr('data-sortField');
                if (!isNullOrBlank(sortField)) {
                    if (sortField === 'applyDate') {
                        var sortType = $('#orgAuthList_sortSwap').attr('data-sortType');
                        if (sortType === 'asc')
                            $.extend(true, postData, {orderCondition: {applyDate: 1}});
                        else if (sortType === 'desc')
                            $.extend(true, postData, {orderCondition: {applyDate: -1}});
                    }else if (sortField === 'auditDate') {
                        var sortType = $('#orgAuthList_sortSwap').attr('data-sortType');
                        if (sortType === 'asc')
                            $.extend(true, postData, {orderCondition: {auditDate: 1}});
                        else if (sortType === 'desc')
                            $.extend(true, postData, {orderCondition: {auditDate: -1}});
                    }
                }

                return postData;
            };
            return {
                url: restApi.url_getAuthenticationPage,
                pageParams: postData,
                success: function (res) {
                    if (res.code == '0') {
                        var data = {};
                        data.pageIndex = $page.pagination('getPageIndex');
                        data.pageSize = $page.pagination('getPageSize');
                        data.list = res.data.list;

                        var html = template('m_orgAuth/m_orgAuth_list_row', data);
                        $(that.element).find('.m-page-data').html(html);
                        that._bindAudit();
                        that._bindAttachPreview();
                    } else {
                        S_toastr.error(res.msg);
                    }
                }
            }
        },
        _refreshList: function (resetPageIndex) {
            var that = this;
            var $page = $(that.element).find('.m-page:first');
            var pageIndex = $page.pagination('getPageIndex');
            if (resetPageIndex === true)
                pageIndex = 0;
            $page.pagination('setPageIndex', pageIndex).pagination('setParams', {}).pagination('remote');
        },
        //搜索
        _bindSearch: function () {
            var that = this;
            $(that.element).find('button[data-action="search"]').click(function () {
                that._refreshList(true);
            });
        },
        //排序
        _bindSortFields: function () {
            var that = this;
            var changed = function (sortField, sortType) {
                that._refreshList(true);
            };
            $('th[name="field_applyDate"]').m_sortableField({
                swapId: '#orgAuthList_sortSwap',
                field: 'applyDate',
                onChanged: changed
            }, true);
            $('th[name="field_auditDate"]').m_sortableField({
                swapId: '#orgAuthList_sortSwap',
                field: 'auditDate',
                onChanged: changed
            }, true);
        },
        _bindAudit: function () {
            var that = this;
            $(that.element).find('a[data-action="audit_pass"]').click(function () {
                var $btn = $(this);
                S_swal.confirm({
                        title: '您确定要批准通过该认证吗？'
                    },
                    function (isConfirm) {
                        if (isConfirm) {
                            var option = {
                                url: restApi.url_authorizeAuthentication,
                                postData: {
                                    id: $btn.attr('data-id'),
                                    authenticationStatus: 2,
                                    auditorName: window.currentAccountName
                                }
                            };
                            m_ajax.postJson(option, function (res) {
                                if (res.code === '0') {
                                    S_toastr.success('提交成功');
                                    that._refreshList();
                                } else {
                                    S_toastr.error(res.msg);
                                }
                            });
                        }
                    });
            });
            $(that.element).find('a[data-action="audit_reject"]').click(function () {
                var $btn = $(this);
                S_layer.panel({
                    area: ['700px', '340px'],
                    content: template('m_orgAuth/m_orgAuth_reject', {}),
                    zIndex: 100,
                    shown: function (index, layero) {
                        $('#rejectType').m_select2_localData({
                            width: '495px',
                            data: that._rejectReasons
                        }, true);
                    },
                    yes: function (index, layero) {
                        var option = {
                            url: restApi.url_authorizeAuthentication,
                            loadingEl: layero.find('.portlet'),
                            postData: {
                                id: $btn.attr('data-id'),
                                authenticationStatus: 3,
                                auditorName: window.currentAccountName,
                                rejectType: layero.find('#rejectType').val(),
                                rejectReason: layero.find('#rejectReason').val()
                            }
                        };
                        m_ajax.postJson(option, function (res) {
                            if (res.code === '0') {
                                S_toastr.success('提交成功');
                                that._refreshList();
                                layer.close(index);
                            } else {
                                S_toastr.error(res.msg);
                            }
                        });
                    }
                });
            });
            $(that.element).find('a[data-action="audit_view"]').click(function () {
                var $btn = $(this);
                S_layer.panel({
                    area: ['700px', '340px'],
                    content: template('m_orgAuth/m_orgAuth_view', {}),
                    zIndex: 100,
                    shown: function (index, layero) {
                        $('#rejectType').m_select2_localData({
                            width: '495px',
                            data: that._rejectReasons
                        }, true);
                        $('#rejectType').val($btn.attr('data-reject-type')).trigger('change').prop("disabled", true);
                        layero.find('#rejectReason').val($btn.attr('data-reject-reason')).prop('readonly', true);
                    }
                });
            });
        },
        _bindAttachPreview: function () {
            var that = this;
            $.each($(that.element).find('a[data-action="preview"]'), function (i, o) {
                $(o).off('click.preview').on('click.preview', function () {
                    var $a = $(this);
                    var pic = [];
                    pic.push({
                        alt: null,
                        pid: null,
                        src: $a.attr('data-url')
                    });
                    var $tr = $a.closest('tr');
                    console.log($tr);
                    $.each($tr.find('a[data-action="preview"]'), function (j, p) {
                        var $p = $(p);
                        if ($p.attr('data-url') !== $a.attr('data-url')) {
                            pic.push({
                                alt: null,
                                pid: null,
                                src: $p.attr('data-url')
                            })
                        }
                    });
                    var photos = {
                            title: '企业认证附件',
                            id: 1,
                            start: 0,
                            data: pic
                        }
                    ;
                    layer.photos({
                        photos: photos,
                        shift: 5
                    });
                })
            });
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);
/**
 * Created by Wuwq on 2017/1/19.
 */
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_page",
        defaults = {
            loadingEl: null,
            pageIndex: 0,
            pageSize: 10,
            total: 0,
            pageBtnCount: 5,
            showFirstLastBtn: false,
            firstBtnText: null,
            lastBtnText: null,
            /*prevBtnText: "&laquo;",
             nextBtnText: "&raquo;",*/
            prevBtnText: '上一页',
            nextBtnText: '下一页',
            loadFirstPage: true,
            remote: {
                url: null,
                params: null,
                pageParams: null,
                success: null,
                beforeSend: null,
                complete: null,
                pageIndexName: 'pageIndex',
                pageSizeName: 'pageSize',
                totalName: 'data.total',
                traditional: false,
                remoteWrongFormat: null
            },
            pageElementSort: ['$page', '$size', '$jump', '$info'],
            showInfo: false,
            infoFormat: '{start} ~ {end} of {total} entires',
            noInfoText: '0 entires',
            showJump: false,
            jumpBtnText: 'Go',
            showPageSizes: false,
            pageSizeItems: [5, 10, 15, 20],
            debug: false
        };

    function Plugin(element, options) {
        this.element = element;
        var remote = $.extend({}, defaults.remote, options.remote);
        this.settings = options;
        this.settings.remote = remote;
        this._defaults = defaults;
        this._name = pluginName;
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            if (that.settings.loadingEl !== null && that.settings.remote.beforeSend === null) {
                that.settings.remote.beforeSend = function () {
                    App.blockUI({
                        target: that.settings.loadingEl,
                        boxed: false,
                        animate: true
                    });
                }
            }
            if (that.settings.loadingEl !== null && that.settings.remote.complete === null) {
                that.settings.remote.complete = function () {
                    App.unblockUI(that.settings.loadingEl);
                }
            }

            if (that.settings.remote.remoteWrongFormat === null) {
                that.settings.remote.remoteWrongFormat = function (res) {
                    if (res && res.code === '500')
                        S_toastr.error('很抱歉，请求发生异常');
                }
            }

            if ($(that.element).pagination())
                $(that.element).pagination('destroy');
            $(that.element).pagination(that.settings);
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);
/**
 * Created by Wuwq on 2017/1/5.
 */
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_select2_localData",
        defaults = {
            allowClear: false,
            language: "zh-CN",
            minimumResultsForSearch: Infinity,
            data: []
        };

    function Plugin(element, options) {
        this.element = element;

        this.settings = options;
        this._defaults = defaults;
        this._name = pluginName;
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            $(that.element).select2(that.settings);
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);
/**
 * Created by Wuwq on 2017/3/6.
 */
;(function ($, window, document, undefined) {

    "use strict";
    var pluginName = "m_sortableField",
        defaults = {
            swapId: null, /*交换区标签Id，用于临时存储当前状态*/
            field: null,
            sortType: '', /* '':没排序 asc：升序 desc：降序*/
            onChanged: null  /*function (sortField,sortType) {}*/
        };

    function Plugin(element, options) {
        this.element = element;

        this.settings = options;
        this._defaults = defaults;
        this._name = pluginName;

        this.settings.field = this.settings.field || $(this.element).attr('name');

        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            that._render();
            that._bindClick();
        }
        , _render: function () {
            var that = this;
            var $el = $(that.element);

            $el.addClass('field-sortable');
            $el.append('<span class="field-sort-indicator"></span>');

            //从交换区恢复或初始化状态
            if (that.settings.swapId && that.settings.swapId !== null) {
                var $swap = $(that.settings.swapId);
                if ($swap.length > 0) {
                    if ($swap.attr('data-sortField') === that.settings.field) {

                        var sortType = $swap.attr('data-sortType');
                        that.settings.sortType=sortType;
                        if (sortType === 'asc')
                            $el.addClass('field-sorted-asc');
                        else if (sortType === 'desc')
                            $el.addClass('field-sorted-desc');

                        //去掉其他列的排序状态
                        $el.closest('th').siblings().each(function (i, o) {
                            var $o = $(o);
                            $o.removeClass('field-sorted-asc');
                            $o.removeClass('field-sorted-desc');
                        });
                    }
                }
            }
        }
        , _bindClick: function () {
            var that = this;
            var $el = $(that.element);

            $el.find('.field-sort-indicator').click(function () {

                $el.removeClass('field-sorted-asc');
                $el.removeClass('field-sorted-desc');

                //去掉其他列的排序状态
                $el.closest('th').siblings().each(function (i, o) {
                    var $o = $(o);
                    $o.removeClass('field-sorted-asc');
                    $o.removeClass('field-sorted-desc');
                });

                /* '':没排序 asc：升序 desc：降序*/
                switch (that.settings.sortType) {
                    case '':
                        that.settings.sortType = 'asc';
                        $(that.element).addClass('field-sorted-asc');
                        break;
                    case 'asc':
                        that.settings.sortType = 'desc';
                        $(that.element).addClass('field-sorted-desc');
                        break;
                    case 'desc':
                        that.settings.sortType = '';
                        break;
                }

                //交换区更新状态
                if (that.settings.swapId && that.settings.swapId !== null) {
                    var $swap = $(that.settings.swapId);
                    if ($swap.length > 0) {
                        if(isNullOrBlank(that.settings.sortType))
                        {
                            $swap.removeAttr('data-sortField');
                            $swap.removeAttr('data-sortType');
                        } else{
                            $swap.attr('data-sortField', that.settings.field);
                            $swap.attr('data-sortType', that.settings.sortType);
                        }
                    }
                }

                if (that.settings.onChanged && that.settings.onChanged !== null)
                    that.settings.onChanged(that.settings.field, that.settings.sortType);
            });
        }
    });

    /*
     1.一般初始化（缓存单例）： $('#id').pluginName(initOptions);
     2.强制初始化（无视缓存）： $('#id').pluginName(initOptions,true);
     3.调用方法： $('#id').pluginName('methodName',args);
     */
    $.fn[pluginName] = function (options, args) {
        var instance;
        var funcResult;
        var jqObj = this.each(function () {

            //从缓存获取实例
            instance = $.data(this, "plugin_" + pluginName);

            if (options === undefined || options === null || typeof options === "object") {

                var opts = $.extend(true, {}, defaults, options);

                //options作为初始化参数，若args===true则强制重新初始化，否则根据缓存判断是否需要初始化
                if (args === true) {
                    instance = new Plugin(this, opts);
                } else {
                    if (instance === undefined || instance === null)
                        instance = new Plugin(this, opts);
                }

                //写入缓存
                $.data(this, "plugin_" + pluginName, instance);
            }
            else if (typeof options === "string" && typeof instance[options] === "function") {

                //options作为方法名，args则作为方法要调用的参数
                //如果方法没有返回值，funcReuslt为undefined
                funcResult = instance[options].call(instance, args);
            }
        });

        return funcResult === undefined ? jqObj : funcResult;
    };

})(jQuery, window, document);

/**
 * Created by Wuwq on 2017/05/26.
 */
var login = {
    init: function () {
        var that = this;
        that._bindAction();
        that._bindKeyDownEnter();

        $.backstretch([
            window.rootPath + '/assets/img/bg/1.jpg',
            window.rootPath + '/assets/img/bg/2.jpg',
            window.rootPath + '/assets/img/bg/3.jpg'
        ], {
            fade: 1000,
            duration: 5000
        });
    },
    _bindAction: function () {
        var that = this;
        $('#btnLogin').click(function () {
            that._login();
        });
    },
    _bindKeyDownEnter: function () {
        var that = this;
        $('#account').keydown(function (e) {
            if (event.keyCode == 13) {
                that._login();
                stopPropagation(e);
                preventDefault(e);
                return false;
            }
        });
        $('#password').keydown(function (e) {
            if (event.keyCode == 13) {
                that._login();
                stopPropagation(e);
                preventDefault(e);
                return false;
            }
        });
    },
    _login: function () {
        var account = $('#account').val();
        var password = $('#password').val();
        if (isNullOrBlank(account) || isNullOrBlank(password)) {
            S_toastr.warning('账号或密码不能为空');
            return;
        }

        var option = {
            url: restApi.url_loginSubmit,
            loadingEl: '',
            postData: {
                account: account,
                password: password
            }
        };
        m_ajax.postJson(option, function (res) {
            if (res.code === '0') {
                S_toastr.success('登陆成功，正在跳转...');
                setTimeout(function () {
                    window.location.href = window.rootPath + '/orgAuth/approveList';
                },1000);
            } else {
                S_toastr.error(res.msg);
            }
        });
    }
};
/**
 * Created by Wuwq on 2017/05/26.
 */
var orgAuth_approveList = {
    init: function () {
        $('.page-content-wrapper').m_orgAuth({},true);
    }
};
/**
 * Created by Wuwq on 2017/07/12.
 */
var main = {
    init: function (pageJs) {
        var that = this;

        that._initSelect2();

        that._initSidebar();

        that._initPageJs(pageJs);
    },
    _initSidebar: function () {
        var $sidebarWrapper = $('.page-sidebar-wrapper');
        if ($sidebarWrapper.length > 0)
            $sidebarWrapper.m_sidebar();
    },
    _initPageJs: function (pageJs) {
        if (pageJs && pageJs.init)
            pageJs.init();
    },
    _initSelect2: function () {
        if ($.fn.select2)
            $.fn.select2.defaults.set("theme", "bootstrap");
    }
};
