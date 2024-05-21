// scripts.js

function openTab(evt, tabName) {
    var i, tabcontent, tablinks;

    // すべてのタブコンテンツを非表示にする
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // すべてのタブリンクから "active" クラスを削除する
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // 現在のタブコンテンツを表示し、現在のタブリンクに "active" クラスを追加する
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
}

// デフォルトで最初のタブを開く
document.addEventListener("DOMContentLoaded", function() {
    document.querySelector(".tab button").click();
});
