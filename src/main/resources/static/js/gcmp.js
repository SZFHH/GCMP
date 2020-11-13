function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return decodeURI(r[2]);
    return null;
}

function handleError(err, isAdmin) {
    if (err.response) {
        var status = err.response.status;
        if (status == 401) {
            if (isAdmin)
                window.location.href = "/admin"
            else
                window.location.href = "/"
        } else {
            $("span.errorMessage").html(err.response.data.message);
            $("div.ErrorMessageDiv").removeClass("fade")
            window.setTimeout(function () {
                $("div.ErrorMessageDiv").addClass("fade")
            }, 2000)
        }
    }
}

function renderSize(value) {
    if (null == value || value == '') {
        return "0 Bytes";
    }
    var unitArr = new Array("Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB");
    var index = 0;
    var srcsize = parseFloat(value);
    index = Math.floor(Math.log(srcsize) / Math.log(1024));
    var size = srcsize / Math.pow(1024, index);
    size = size.toFixed(2);//保留的小数位数
    return size + unitArr[index];
}

function formatDate(date, fmt) {
    var o = {
        "M+": date.getMonth() + 1,                 //月份
        "d+": date.getDate(),                    //日
        "h+": date.getHours(),                   //小时
        "m+": date.getMinutes(),                 //分
        "s+": date.getSeconds(),                 //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function syncSelect() {
    var selectAll = true;
    $(".selectSingle").each(function () {
        if ("false" == $(this).attr("selectedd")) {
            selectAll = false;
        }
    });
    if (selectAll) {
        $("img.selectAll").attr("src", "/img/selected.png");
        $("img.selectAll").attr("selectedd", "true");
    } else {
        $("img.selectAll").attr("src", "/img/notSelected.png");
        $("img.selectAll").attr("selectedd", "false");
    }

}

function getSelectedList() {
    var arr = $(".selectSingle");
    var rv = [];
    arr.each(function () {
        var selectedd = $(this).attr("selectedd");
        if (selectedd == "true") {
            var idx = $(this).attr("idx");
            rv.push(idx);
        }
    })
    return rv;
}

function notselectAll() {
    $(".selectSingle").each(function () {
        $(this).attr("src", "/img/notSelected.png");
        $(this).attr("selectedd", "false");
    });
    $(".selectAll").each(function () {
        $(this).attr("src", "/img/notSelected.png");
        $(this).attr("selectedd", "false");
    });
}

function showErrMsg(msg, endu) {
    $("span.errorMessage").html(msg);
    $("div.ErrorMessageDiv").removeClass("fade");
    if (endu > 0) {
        window.setTimeout(function () {
            $("div.ErrorMessageDiv").addClass("fade")
        }, endu)
    }
}

function hidErrMsg() {
    $("div.ErrorMessageDiv").addClass("fade");
}
