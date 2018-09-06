jQuery(document).ready(function() {
    init();
    // dataTableInit();

    $('#back').click(function () {
        var tabId = $('#back').data('tabId');
        AjaxTool.getHtml('processRecord/Record',function (html) {
            $('.page-content').html(html);
            $('#'+tabId).trigger('click');
        });
    });
});

function init() {
    // DatatableTool.initDatatable("process-info-table",{
    //     info: false,
    //     bFilter: false,    //去掉搜索框方法三：这种方法可以
    //     bLengthChange: false
    // }, [{
    //     'width': '20%',
    //     'targets': 0
    // }, {
    //         'orderable' : false,
    //         'targets' : [ 1,2 ]
    // }]);
    $("#process-info-table").dataTable({
        info: false,
        bFilter: false,    //去掉搜索框方法三：这种方法可以
        bLengthChange: false,
        "oLanguage":{"sZeroRecords": "没有匹配结果"},
        "columnDefs": [
            {width: '20%', targets: 0}
        ]
    });
}

// function seeUsers(roleId){
//     AjaxTool.get('processRecord/seeUsers',{roleId: roleId},function (data) {
//         if(data.result){
//             DatatableTool.modalShow("#user-modal");
//             var list = data.obj;
//             $("#user-info").empty();
//             for(var i = 0; i < list.length; i++){
//                 var p = document.createElement('p');
//                 p.innerHTML = list[i];
//                 $("#user-info").append(p);
//             }
//         }
//     });
// }