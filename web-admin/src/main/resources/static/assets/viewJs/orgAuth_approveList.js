/**
 * Created by Wuwq on 2017/05/26.
 */
var orgAuth_approveList = {
    init: function () {
        var that = this;
        that._render();
    },
    _render:function () {
        var that = this;
        var html = template('m_orgAuth/m_approveList', {});
        $('.page-content-wrapper').html(html);
        that._initTable();
    },
    _initTable:function(){
        var table = $('#list');
        table.dataTable({
            bStateSave: true, // save datatable state(pagination, sort, etc) in cookie.
            lengthMenu: [
                [5, 15, 20, -1],
                [5, 15, 20, "All"] // change per page values here
            ],
            columnDefs: [
                {  // set default column settings
                    'orderable': false,
                    'targets': [0]
                },
                {
                    "searchable": false,
                    "targets": [0]
                },
                {
                    "className": "dt-right",
                    //"targets": [2]
                }
            ],
            pageLength: 10,
            pagingType: "bootstrap_full_number",
            processing: false,
            serverSide: true,
            ajax: {
                url: restApi.url_getAuthenticationPage,
                type: 'POST',
                data: function (d) {
                    var postData={};
                    postData.pageIndex = d.start;
                    postData.pageSize = d.length;
                    return JSON.stringify(postData);
                },
                dataFilter: function (data) {
                    var json = jQuery.parseJSON(data);
                    var result={};
                    result.recordsTotal = json.data.total;
                    result.recordsFiltered = json.data.total;
                    result.data = json.data.list||[];
                    result.draw = 1;
                    console.log(result);
                    return JSON.stringify(result); // return JSON string
                },
                contentType: "application/json"
            },
            createdRow: function (row, data, dataIndex) {
                /*var html = template('m_projectList/m_projectList_row', {p: data});
                $(row).html(html);*/
                console.log(data);
            }
        });
    }
};