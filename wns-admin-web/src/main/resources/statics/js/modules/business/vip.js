$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + '/vip/user/list',
        datatype: "json",
        colModel: [
            { label: 'id', name: 'id', sortable: false, width: 30, key: true },
            { label: '昵称', name: 'nickName', sortable: false, width: 60 },
            { label: '头像', name: 'headImg', sortable: false, width: 35,
                formatter: function(v) {
                    return v ? '<img src="'+ v +'" width="40" height="40">' : '';
                }
            },
            { label: '余额', name: 'amount', sortable: false, width: 40 },
            { label: '手机号码', name: 'mobile', sortable: false, width: 60 },
            { label: '充值记录', name: 'recharge', sortable: false, width: 60,
                formatter: function(value, options, row) {
                    return '￥ '+ value + '-<a onclick="chargeList('+ row.id +', 1)">查看</a>';
                }
            },
            { label: '提现记录', name: 'withdraw', sortable: false, width: 60,
                formatter: function(value, options, row) {
                    return '￥ '+ value + '-<a onclick="chargeList('+ row.id +', 2)">查看</a>';
                }
            },
            { label: '下注记录', name: 'chipIn', sortable: false, width: 60,
                formatter: function(value, options, row) {
                    return '￥ '+ value + '-<a onclick="orderList('+ row.id +')">查看</a>';
                }
            },
            { label: '累计赚取', name: 'earn', sortable: false, width: 60,
                formatter: function(value, options, row) {
                    return '￥ '+ value + '-<a onclick="userFlowRecords('+ row.id +',3,1)">查看</a>';
                }
            },
            { label: '累计佣金', name: 'commission', sortable: false, width: 60,
                formatter: function(value, options, row) {
                    return '￥ '+ value + '-<a onclick="userFlowRecords('+ row.id +',5,1)">查看</a>';
                }
            },
            { label: '注册时间', name: 'createTime', sortable: false, width: 80 },
            { label: '操作', name: 'operation', sortable: false, width: 140, formatter: function(value, options, row){
                let isNormal = row['status'] === 0;
                let status_btn = '<input type="button" class="btn btn-sm btn-'+ (isNormal ? 'danger' : 'primary') +'" ' +
                    'onclick="updateStatus('+ row.id +','+ (isNormal ? 1 : 0) +')" value="'+ (isNormal ? '禁用' : '恢复') +'"/>';
                let relation_btn = '<a class="btn btn-sm btn-primary" onclick="relationUser('+ row.id +')"><i class="fa fa-sitemap"></i>上下级用户</a>';
                let charge_btn = '<a class="btn btn-sm btn-primary" onclick="changeHandle('+ row.id +')"><i class="fa fa-money"></i>充值</a>';
                return charge_btn + '&nbsp;' + relation_btn + '&nbsp;' + status_btn;
            }}
        ],
        viewrecords: true,
        height: 450,
        rowNum: 10,
        rowList : [10,30,50],
        // rownumbers: true,
        // rownumWidth: 25,
        autowidth:true,
        // multiselect: true,
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
        postData: {createTime: getParams("createTime")},
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

    laydate.render({
        elem: '#qCreateTime', //指定元素
        done: function(value, date, endDate){
            vm.q.createTime = value;
        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            mobile: null,
            id: null,
            nickName: null,
            createTime: getParams("createTime")
        },
        showList: true,
        title: null,
        user: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        del: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "vip/user/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(index){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            var url = vm.user.id == null ? "vip/user/save" : "vip/user/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.user),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'mobile': vm.q.mobile, 'id': vm.q.id, 'nickName' : vm.q.nickName, 'createTime': vm.q.createTime},
                page:page
            }).trigger("reloadGrid");
        }
    }
});

function update(id) {
    $.get(baseURL + "vip/user/info/"+id, function(r){
        vm.showList = false;
        vm.title = "修改";
        vm.user = r.user;
    });
}

function chargeList(id, type) {
    window.location.href = 'charge.html?vipId='+id+'&type='+type;
}

function orderList(id) {
    window.location.href = 'order_manager.html?vipId='+id;
}

function userFlowRecords(id, type, status) {
    window.location.href = 'user_flowRecords.html?vipId='+id+'&type='+type+'&status='+status;
}

function changeHandle(id) {
    window.location.href = 'charge.html?vipId='+id+'&type='+1+'&handle=charge';
}

function relationUser(id) {
    window.location.href = 'vip_relation.html?vipId='+id;
}

function updateStatus(id, status) {
    confirm('确定要'+ (status === 0 ? '恢复' : '禁用') +'该用户？', function() {
        $.ajax({
            type: "POST",
            url: baseURL + 'vip/user/updateStatus',
            // contentType: "application/json",
            data: {'id': id, 'status': status},
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

