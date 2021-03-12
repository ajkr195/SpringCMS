async function getUser(id) {
	let url = '/api/findappuser/' + id;
	try {
		let response = await fetch(url);
		return await response.json();
	} catch (error) {
		console.log(error);
		//alert(error);
	}
}

async function displayUsers(id) {
	let user = await getUser(id);
	//alert(JSON.stringify(user));
	document.getElementById("userId").innerHTML = user.id;
	document.getElementById("userName").innerHTML = user.username;
	document.getElementById("userEmail").innerHTML = user.useremail;
	document.getElementById("userFirstname").innerHTML = user.userfirstname;
	document.getElementById("userLastname").innerHTML = user.userlastname;
	document.getElementById("userAddress").innerHTML = user.useraddress;
	document.getElementById("userDatecreated").innerHTML = user.creationDate;
	document.getElementById("userCreatedby").innerHTML = user.createdBy;
	document.getElementById("userDatemodified").innerHTML = user.lastModifiedDate;
	document.getElementById("userModifiedby").innerHTML = user.lastModifiedBy;

	let userroles = "";
	user.roles.forEach((role) => {
		userroles += role.name + " ";
	});

	document.getElementById("userRoles").innerHTML = userroles;
}


async function deleteTheRecord(td, id) {
	var url = "/api/deleteuser/" + id;
	await fetch(url, { method: 'DELETE', headers: { 'Accept': 'application/json' } })
		.then((response) => {

			if (response.status >= 403) {
				//throw new Error("You do not have permissions for this operation.");
				openAccessDeniedModal();
			}

			else if (response.status >= 400 && response.status < 600) {
				throw new Error("Bad response from server");
			}

			else if (response.status === 200) {
				openDeleteConfirmationModal();
				var table = document.getElementById("userTable");
				row = td.parentElement.parentElement.parentElement;
				table.deleteRow(row.rowIndex);
			}
			//alert("response.status  ::  "+response.status);
		})
		.catch((error) => {
			alert(error)
		});
}


function openDeleteConfirmationModal() {
	var myModal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'), { backdrop: 'static', keyboard: false });
	myModal.show();
}

function openAccessDeniedModal() {
	var myModal = new bootstrap.Modal(document.getElementById('accessDeniedModal'), { backdrop: 'static', keyboard: false });
	myModal.show();
}


document.getElementById("sidebarCollapse").addEventListener('click',
	function() {
		document.getElementById("sidebar").classList.toggle("active");
		this.classList.toggle("active");
	});



const container = document.querySelector('.mainsidebar');
let lastSelectedIcon;
container.addEventListener('click', (event) => {
	if (lastSelectedIcon) {
		lastSelectedIcon.classList.remove("down");
	}
	const icon = event.target.closest('.ddmenu')
		.querySelector('.fa-chevron-right');
	if (null != icon) {
		icon.classList.add('down');
		lastSelectedIcon = icon;
	}
});




var mainparentelements = document.querySelectorAll('.ddmenu');
for (let i = 0; i < mainparentelements.length; i++) {
	mainparentelements[i].onclick = function() {
		var c = 0;
		while (c < mainparentelements.length) {
			mainparentelements[c++].classList.remove("selected");
		}
		mainparentelements[i].classList.add("selected");
	};
}



const alllistitems = document.querySelectorAll(".buttons-container li");
const alllistlinks = document.querySelectorAll(".buttons-container li a");
for (let i = 0; i < alllistitems.length; i++) {
	alllistitems[i].addEventListener("click", function() {
		for (let i = 0; i < alllistitems.length; i++) {
			alllistitems[i].classList.remove("active");
			alllistlinks[i].classList.remove("active-text");
		}
		this.classList.add("active");
		alllistlinks[i].classList.add("active-text");
	});
}


function toggleBtnCntnr() {
	var toggleBtnCntnr = document.getElementById("buttons-container");
	if (toggleBtnCntnr.style.display === "block") {
		toggleBtnCntnr.style.display = "none";
	} else {
		toggleBtnCntnr.style.display = "block";
	}
}


function toggleSearchCntnr() {
	//alert('Hello');
	if (document.getElementById("searchcontainer")) {
		var searchCntnr = document.getElementById("searchcontainer");
		if (searchCntnr.style.display === "block") {
			searchCntnr.style.display = "none";
		} else {
			searchCntnr.style.display = "block";
		}
	} else {
		return false;
	}
}

//function cloneTheSearchDiv() {
//  var itm = document.getElementById("clonethisdiv");
//  var cln = itm.cloneNode(true);
//  document.getElementById("clonethisdiv").appendChild(cln);
//}

const themeCookieName = 'theme';
const themeDark = 'dark';
const themeLight = 'light';
const body = document.getElementsByTagName('body')[0];
function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/"
}
function getCookie(cname) {
	var name = cname + "="
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return ""
}
loadTheme();
function loadTheme() {
	var theme = getCookie(themeCookieName);
	body.classList.add(theme === "" ? themeLight : theme);
}
function switchTheme() {

	if (body.classList.contains(themeLight)) {
		body.classList.remove(themeLight);
		body.classList.add(themeDark);
		setCookie(themeCookieName, themeDark);
	} else {
		body.classList.remove(themeDark);
		body.classList.add(themeLight);
		setCookie(themeCookieName, themeLight);
	}
}

function openOverlayNav() {
	document.getElementById("sidebarOverlayNav").style.width = "100%";
}

function closeOverlayNav() {
	document.getElementById("sidebarOverlayNav").style.width = "0%";
}

(function() {
	'use strict'

	// Fetch all the forms we want to apply custom Bootstrap validation styles to
	var forms = document.querySelectorAll('.needs-validation')

	// Loop over them and prevent submission
	Array.prototype.slice.call(forms).forEach(function(form) {
		form.addEventListener('submit', function(event) {
			if (!form.checkValidity()) {
				event.preventDefault()
				event.stopPropagation()
			}

			form.classList.add('was-validated')
		}, false)
	})
})()



function removeRow(r) {
	var fooId = "foo";
	var id = r.id.substring(3, r.id.length - 1);
	for (i = 1; i <= 3; i++) {
		document.getElementById(fooId + id + i).remove();
	}
	document.getElementById(fooId + id + 5).nextSibling.remove();
	document.getElementById(fooId + id + 5).remove();
}
var x = 1;
function addMoreSearchFields() {
	var fooId = "foo";
	for (i = 1; i <= 3; i++) {
		//Create an input type dynamically.
		var element = document.createElement("input");
		//Assign different attributes to the element.
		element.setAttribute("type", fooId + x + i);
		element.setAttribute("name", fooId + x + i);
		element.setAttribute("id", fooId + x + i);
		var foo = document.getElementById("fooBar");
		foo.appendChild(element);
		foo.innerHTML += ' ';
	}
	i++;
	var element = document.createElement("input");
	element.setAttribute("type", "button");
	element.setAttribute("value", "X");
	element.style.backgroundColor = "#dc3545";
	element.style.color = "#ffff";
	element.setAttribute("id", fooId + x + i);
	element.setAttribute("name", fooId + x + i);
	element.setAttribute("onclick", "removeRow(this)");
	foo.appendChild(element);
	var br = document.createElement("br");
	foo.appendChild(br);
	x++;
}
