$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/withdrawAudit/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 30, key: true },
            { label: '用户id', name: 'userId', width: 30 },
            { label: '用户昵称', name: 'nickName', width: 30 },
            { label: '提现金额(元)', name: 'amount', width: 60 },
            { label: '支付订单号', name: 'orderId', width: 60 },
            { label: '审核状态', name: 'auditStatus', width: 60, formatter: function(value, options, row){
                    var array = ['待审核','审核通过','审核不通过'];
                    return array[value];
                }},
            { label: '交易状态', name: 'sign', width: 60 , formatter: function(value, options, row){
                    var array = ['待交易','成功','失败'];
                    return array[value];
                }},
            { label: '创建时间', name: 'createTime', width: 60 }
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        postData: {auditStatus: 0},
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            userId: null,
            nickName: '',
            auditStatus: 0
        },
        showList: true,
        title: null,
        record: {},
        auditStatusData:[
            {name:"请选择审核状态",value: ''},
            {name:"待审核",value: 0},
            {name:"审核通过",value: 1},
            {name:"审核不通过",value: 2},
        ]
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'userId': vm.q.userId, 'nickName': vm.q.nickName, 'auditStatus': vm.q.auditStatus},
                page:page
            }).trigger("reloadGrid");
        },
        auditPass: function() {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            var auditStatus = $("#jqGrid").getRowData(id).auditStatus;
            if ('待审核' !== auditStatus) {
                alert('该记录无需审核');
                return;
            }

            confirm('确定审核通过选中的记录？', function() {
                $.ajax({
                    type: "POST",
                    url: baseURL + 'user/withdrawAudit/audit',
                    contentType: "application/json",
                    data: JSON.stringify({'id': id, 'auditStatus': 1}),
                    success: function (r) {
                        if (r.code === 0) {
                            alert('操作成功', function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        auditNotPass: function() {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            var auditStatus = $("#jqGrid").getRowData(id).auditStatus;
            if ('待审核' !== auditStatus) {
                alert('该记录无需审核');
                return;
            }

            confirm('确定审核不通过选中的记录？', function() {
                $.ajax({
                    type: "POST",
                    url: baseURL + 'user/withdrawAudit/audit',
                    contentType: "application/json",
                    data: JSON.stringify({'id': id, 'auditStatus': 2}),
                    success: function (r) {
                        if (r.code === 0) {
                            alert('操作成功', function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        }
    }
});