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
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {
            var that = this;
            that._render();
        },
        _render: function () {
            var that = this;
            var html = template('m_orgAuth/m_approveList', {});
            $(that.element).html(html);
            that._bindPage();
            that._bindSearch();
        },
        _bindPage: function () {
            var that = this;
            var $page = $(that.element).find('.m-page:first');
            console.log($page);
            var remote = that._remote();
            $page.m_page({
                loadingId: '.m-page-data',
                pageSize: 20,
                remote: remote
            }, true);
        },
        _remote: function () {
            var that = this;
            var $page = $(that.element).find('.m-page:first');
            var postData = function (data) {
                var postData = {pageIndex: data.pageIndex, pageSize: data.pageSize};
                $.extend(true, postData, {filterStatus: 1});
                var searchKey = $(that.element).find('.search-input').val();
                if (!isNullOrBlank(searchKey))
                    $.extend(true, postData, {orgNameMask: searchKey, orgAliasMask: searchKey});
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

                        var html = template('m_orgAuth/m_approveList_row', data);
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
            console.log(pageIndex);
            $page.pagination('setPageIndex', pageIndex).pagination('setParams', {}).pagination('remote');
        },
        _bindSearch: function () {
            var that = this;
            $(that.element).find('button[data-action="search"]').click(function () {
                that._refreshList(true);
            });
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
                            var option = {};
                            option.url = restApi.url_authorizeAuthentication;
                            option.classId = '';
                            option.postData = {id: $btn.attr('data-id'), authenticationStatus: 2};
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