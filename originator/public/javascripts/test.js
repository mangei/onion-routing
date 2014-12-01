$(function () {
    console.log("site loaded");

    $('#send-btn').on('click', function (e) {
        $.post('/request',
            function (response) {
                console.log(response);

                if (response.payload && response.usedNodes) {
                    var html = '<li class="quotation-list-element">';

                    var quote = JSON.parse(response.payload);

                    if (quote.quote) {
                        html += '<blockquote><p>';
                        html += quote.quote;
                        html += '</p></blockquote>';
                    }


                    html += '<ul class="list-inline">';
                    response.usedNodes.forEach(function(node) {
                        html += '<li>';
                        html += '<span class="label label-default">';
                        html += node;
                        html += '</span>';
                        html += '</li>';
                    });
                    html += '</ul>';

                    html += '</li>';
                    $('#quote-list').append(html);
                }
            })
            .fail(function () {
                console.log('request failed - check the logs');
            })
    });
});