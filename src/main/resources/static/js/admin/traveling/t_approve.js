var tabs = document.getElementById('tab').getElementsByTagName('button');
var roleName = $('#roleName').val();
for(var i=0; i<tabs.length; i++) {
    tabs[i].onclick = function () {
        var tabId = this.id;
        //若当前按钮已选中则点击不再触发事件
        if($(this).hasClass('button')) {return};
        AjaxTool.get('traveling/sqjlTab', {
                lx: this.id, bz: 'sp'},function (data) {
                if(data.success) {
                    var str = "";
                    var tras = data.content;
                    for(var i=0;i<tras.length;i++) {
                        str += "<tr id='"+tras[i].id+"'>";
                        str += "<td>"+tras[i].name+"</td>";
                        str += "<td>"+tras[i].sqsj+"</td>";
                        if(tras[i].spyj == null || tras[i].spyj == undefined)
                            str += "<td>-</td>";
                        else str += "<td>"+tras[i].spyj +"</td>";
                        switch (tabId) {
                            case "dsp":
                                str += "<td>待审批</td>";
                                str += "<td><div onclick='seeApprove(\""+tras[i].id +'\",\"'+ tabId+"\")'>查看</div>" +
                                    "<div onclick='pass(\"" + tras[i].id +"\")'>通过</div></td>";
                                break;
                            case "yth":
                                str += "<td>已撤回</td>";
                                str += "<td><div onclick='seeApprove(\""+tras[i].id +'\",\"'+ tabId+"\")'>查看</div></td>";
                                break;
                            default:
                                var ccshzt = tras[i].ccshzt;
                                if(ccshzt == '通过'){
                                    str += "<td><div style='color:green'>"+ccshzt+"</div></td>";
                                }
                                else if(ccshzt == '终止' || ccshzt == '流程停用') {
                                    str += "<td><div style='color: red'>"+ccshzt+"</div></td>";
                                }
                                else{
                                    str += "<td><div>"+tras[i].ccshzt+"</div></td>";
                                }
                                str += "<td><div onclick='seeApprove(\""+tras[i].id +'\",\"'+ tabId+"\")'>查看</div></td>";
                                break;
                        }
                        str += "</tr>";
                    }
                    var mTable = $('#t-approve-table').DataTable();
                    mTable.destroy();
                    $('#t-approve-tbody').html(str);
                    init();
                }
            }
        );
        $(this).removeClass("button-active").addClass("button");
        for(var j=0;j<tabs.length;j++) {
            if(tabs[j] != this) {
                $(tabs[j]).removeClass("button").addClass("button-active")
            }
        }
    }
}


function seeApprove(id,tabId) {
    AjaxTool.html('traveling/ccspckxq',{id: id},function (html) {
        $('.portlet-body').html(html);
        switch (tabId) {
            case "ysp":
                $('.operation').attr('style','display:none');
                break;
            default:
                break;
        }
        $('#back').data('tabId',tabId);
        // if(roleName !=='法务') {
        //     $('.yj-input').html("<input type='hidden' name='cljg' value='已审核'/>");
        // }
    });
}

function pass(id) {
    var isConfir =confirm('您确认通过吗?');
    if(isConfir==true){
        AjaxTool.post('traveling/addLcrz',{
            cljg: '通过', id:id},function (data) {
            alert(data.message);
            var table = $('#t-approve-table').DataTable();
            if(data.success) {
                table.rows('#'+id).remove().draw();
                refreshTraveling();
            }
        });
    }

}

function refreshTraveling(){
    var n = $('#出差审批').text();
    n--;
    if(n == 0){
        $('#出差审批').remove();
    }else{
        $('#出差审批').text(n);
    }
    var count = $('#出差管理').text();
    count--;
    if (count == 0){
        $('#出差管理').remove();
    }else{
        $('#出差管理').text(count);
    }
}

function init() {
    DatatableTool.initDatatable("t-approve-table", [{
            'orderable' : false,
            'targets' : [ 4 ]
        }, {
            "searchable" : false,
            "targets" : [ 4 ]
        }], [ [ 1, "desc" ] ]);
}

jQuery(document).ready(function() {
    init();
});
