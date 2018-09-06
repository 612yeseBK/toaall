$('#print').click(function() {
    var id=$('#id').html();
    var isConfir =confirm('您确认打印吗?');
    if(isConfir==true){
        window.location = "/wjsc?xgdm=jtrs&xgid=" +id;
    }
});



//
// $('.date-picker').datetimepicker({
//     format:'yyyy/mm/dd',
//     language: 'zh-CN',
//     weekStart: 1,
//     todayBtn:  1,
//     autoclose: 1,
//     todayHighlight: 1,
//     startView: 2,
//     minView: 2,
//     forceParse: 0
// });

// var validator = $('#t_apply_print_form').validate({
//     errorElement: 'span', //default input error message container
//     errorClass: 'error-tips', // default input error message class
//     rules: {
//         yfrq :{
//             required: true,
//             dateISO: true
//         }
//     },
//     messages: {
//         dyrq: {
//             dateISO: "日期格式不正确"
//         },
//     }
// });

$('#back').click(function () {
    var tabId = ($('#back').data('tabId'));
    AjaxTool.getHtml('traveling/sqjl',function (html) {
        $('.page-content').html(html);
        $('#'+tabId).trigger('click');
    });
});

jQuery(document).ready(function() {
    var o = document.getElementsByClassName("print-content-shInfo");
    var size = o.length;
    var height = size * 80 + 240 + 2;
    var form = document.getElementById("t-print-body");
    form.setAttribute('style', 'height: '+height+'px');

});
