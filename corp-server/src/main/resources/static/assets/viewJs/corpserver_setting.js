/**
 * Created by Wuwq on 2017/05/26.
 */
var corpserver_setting = {
    init: function () {
        var that = this;
        that.bindAdd();
        that.refreshData();
    },
    refreshData: function () {
        var that = this;
        axios.get('/corpserver/syncCompany/selectAll')
            .then(function (res) {
                if (res.data && res.data.code) {
                    if (res.data.code === '0') {
                        $('#tbody').html('');
                        $.each(res.data.data, function (i, o) {
                            $('#tbody').append(_.sprintf('<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td> <a name="btnRemove" href="javacript:void(0)" data-id="%s" class="btn btn-danger btn-sm ">删除</a>&nbsp;<a name="btnSync" href="javacript:void(0)" data-id="%s" class="btn btn-danger btn-sm ">立即同步</a></td></tr>', o.corpEndpoint, o.companyId, $.trim(o.companyName).length === 0 ? '<span style="color:red;">无法匹配</span>' : o.companyName, $.trim(o.remarks), o.id, o.companyId))
                        });

                        that.bindAction();
                    }
                    else
                        alert("数据请求失败，" + res.data.msg);
                } else {
                    alert("数据请求失败");
                }
            })
            .catch(function (err) {
                console.error(err);
            });
    },
    bindAdd: function () {
        var that=this;
        $('#btnAdd').click(function () {
            var corpEndpoint = $.trim($('#corpEndpoint').val());
            var companyId = $.trim($('#companyId').val());
            var remarks = $.trim($('#remarks').val());

            axios.post('/corpserver/syncCompany/create', {
                corpEndpoint: corpEndpoint,
                companyId: companyId,
                remarks: remarks
            }).then(function (res) {
                if (res.data && res.data.code) {
                    console.log(res);
                    if (res.data.code === '0')
                        that.refreshData();
                    else
                        alert("数据提交失败，" + res.data.msg);
                } else {
                    alert("数据提交失败");
                }
            }).catch(function (err) {
                console.error(err);
            });
        });
    }
    , bindAction: function () {
        var that=this;
        $('a[name="btnRemove"]').click(function () {
            var id = $(this).attr('data-id');
            axios.post('/corpserver/syncCompany/delete/' + id, {}).then(function (res) {
                if (res.data && res.data.code) {
                    console.log(res);
                    if (res.data.code === '0')
                        that.refreshData();
                    else
                        alert("数据提交失败，" + res.data.msg);
                } else {
                    alert("数据提交失败");
                }
            }).catch(function (err) {
                console.error(err);
            });
        });

        $('a[name="btnSync"]').click(function () {
            var id = $(this).attr('data-id');
            axios.post('/corpserver/syncCompany/pushSyncAllCmd/' + id, {}).then(function (res) {
                if (res.data && res.data.code) {
                    console.log(res);
                    if (res.data.code !== '0')
                        alert("数据提交失败");
                } else {
                    alert("数据提交失败");
                }
            }).catch(function (err) {
                console.error(err);
            });
        });
    }
};