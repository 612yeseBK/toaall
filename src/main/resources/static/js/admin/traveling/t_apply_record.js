var tabs = document.getElementById('tab').getElementsByTagName('button');
for(var i=0; i<tabs.length; i++) {
    tabs[i].onclick = function () {
        var tabId = this.id;
        //若当前按钮已选中则点击不再触发事件
        if($(this).hasClass('button')) {return};
        AjaxTool.get('traveling/sqjlTab', {
                lx: this.id, bz: 'sq'},function (data) {
                if(data.success) {
                    var str = "";
                    var tras = data.content;
                    for(var i=0;i<tras.length;i++) {
                        str += "<tr id='"+tras[i].id+"'>";
                        str += "<td>"+tras[i].name+"</td>";
                        str += "<td>"+tras[i].sqsj+"</td>";
                        switch(tabId){
                            case "wtj":
                                str += "<td>未提交</td>";
                                str += "<td><div onclick='seeApplyRecordE(\""+ tras[i].id +'\",\"' + tabId +"\")'>查看</div>" +
                                    "<div onclick='submitWtjForm(\""+ tras[i].id +"\")'>提交</div>" +
                                    "<div onclick='deleteWtjCon(\""+ tras[i].id +"\")'>删除</div></td>";
                                break;
                            case "dsp":
                                str += "<td>待审批</td>";
                                str += "<td><div onclick='seeApplyRecordNE(\""+ tras[i].id +'\",\"' + tabId +"\")'>查看</div>" +
                                    "<div onclick='revoke(\""+ tras[i].id +"\")'>撤回</div></td>";
                                break;
                            case "ysp":
                                var ccshzt = tras[i].ccshzt;
                                if(ccshzt == '通过'){
                                    str += "<td><div style='color:green'>"+ccshzt+"</div></td>";
                                    str += "<td><div onclick='seeApplyRecordNE(\""+ tras[i].id +'\",\"' + tabId +"\")'>查看</div>" +
                                    "<div onclick='printCc(\""+ tras[i].id +'\",\"' + tabId +"\")'>打印</div></td>";
                                }
                                else if(ccshzt == '终止') {
                                    str += "<td><div style='color: red'>"+ccshzt+"</div></td>";
                                    str += "<td><div onclick='seeApplyRecordNE(\""+ tras[i].id +'\",\"' + tabId +"\")'>查看</div></td>";
                                }
                                break;
                            case "yth":
                                var ccshzt = tras[i].ccshzt;
                                if(ccshzt == 'deactivation'){
                                    str += "<td>流程停用</td>";
                                }else if(ccshzt == 'revocation'){
                                    str += "<td>已撤回</td>";
                                }
                                str += "<td><div onclick='seeApplyRecordNE(\""+ tras[i].id +'\",\"' + tabId +"\")'>查看</div></td>";
                                break;
                            default:
                                break;
                        }
                        str += "</tr>";
                    }
//                    mTable.fnClearTable();
                    var mTable = $('#t-apply-record-table').DataTable();
                    mTable.destroy();
                    $('#t-record-tbody').html(str);
                    init();
                }
            }
        );
        $(this).removeClass("button-active").addClass("button");
        for(var j=0;j<tabs.length;j++) {
            if(tabs[j] != this) {
                $(tabs[j]).removeClass("button").addClass("button-active");
            }
        }
    }
}


function init() {//dataTable初始化
    mTable=DatatableTool.initDatatable("t-apply-record-table", [ {
        'orderable' : false,
        'targets' : [ 3 ]
    }, {
        "searchable" : false,
        "targets" : [ 3 ]
    }], [ [ 1, "desc" ] ]);
}

function seeApplyRecordE(id,tabId){
    AjaxTool.html('traveling/sqjlxqE',{id: id},function (html) {
        $('.portlet-body').html(html);
        // if(tabId == "yth") {
        //     $('#splc').attr('style','display:block');
        // }
        $('#back').data('tabId',tabId);
    });
}

function seeApplyRecordNE(id, tabId){
    AjaxTool.html('traveling/sqjlxqNE',{id: id},function (html){
        $('.portlet-body').html(html);
        if(tabId == 'yth') {
            $('#fjck').attr('style','display:none');
        }
        $('#back').data('tabId',tabId);
    });
}

function submitWtjForm(id){
    AjaxTool.post('traveling/submitWtj',{id: id}, function (data) {
            alert(data.message);
            var table = $('#t-apply-record-table').DataTable();
            if(data.success) {
                table.rows('#' + id).remove().draw();
            }
        }
    )
}

function printCc(id,tabId){
    AjaxTool.html('traveling/printTg',{id: id},function (html) {
        $('.portlet-body').html(html);
        $('#back').data('tabId',tabId);
    });
}

function deleteWtjCon(id) {
    AjaxTool.post('traveling/deleteWjt',{
        id:id},function (data) {
        alert(data.message);
        var table = $('#t-apply-record-table').DataTable();
        if(data.success) {
            table.rows('#' + id).remove().draw();
        }
    })
}

function revoke(id) {
    var s = "<form id='revoke-form'><select id='select' class='form-control'>" +
        "<option value='申请人发现有误'>提交人发现有误</option>" +
        "<option value='其他'>其他</option></select>" +
        "<input type='text' name='reason' placeholder='选择其他时理由自填' class='form-control' style='margin:20px 0'/></form>";
    SweetAlert.swalContent("确认撤回",s,function () {
        var reason = $('#select').val();
        if(reason === '其他') {
            reason = $('#revoke-form input[name="reason"]').val();
        }
        AjaxTool.post('traveling/revocationTraveling',{
            id:id,revocationReason:reason},function (data) {
            var table = $('#t-apply-record-table').DataTable();
            if(data.success) {
                table.rows('#' + id).remove().draw();
            }
        })
    })
}

jQuery(document).ready(function() {
    init();
});