
const toggleSidebar = () => {
    
    if($(".sidebar").is(":visible")){
        
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");

    }else{

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");

    }
};

const search = () => {
  console.log("searching");

  let query = $("#search-input").val();

  if(query == ''){
    $(".search-result").hide();
  }else{
    
    let url = `http://localhost:8080/search/${query}`;

    fetch(url).then((response) => 
    {
      return response.json();
    })
    .then((data) => 
    {
      console.log(data);

      let text = `<div class='list-group'>`;

      data.forEach((contact) => {
        text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name} </a> `
      });

      text += `</div>`;

      $(".search-result").html(text);
      $(".search-result").show();
    });
    
  }
  
};



/*function openNav() {
  document.getElementById("mySidebar").style.width = "250px";
  document.getElementById("main").style.marginLeft = "250px";
}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
/*function closeNav() {
  document.getElementById("mySidebar").style.width = "0";
  document.getElementById("main").style.marginLeft = "0";
 
}*/