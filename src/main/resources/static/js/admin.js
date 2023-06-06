$(async function () {
    await fetchUsers();
    await loadRolesAndSubmitForm();
    await fillLoggedUserPage();
})

async function fetchRoles() {
    const url = 'http://localhost:8080/admin/roles';
    const response = await fetch(url);

    if (response.ok) {
        return await response.json();
    } else {
        throw new Error('Failed to fetch roles');
    }
}

async function editUser(user) {
    const modal = new bootstrap.Modal(document.querySelector('#editModal'));
    const idInput = document.querySelector('#editId');
    const nameInput = document.querySelector('#editName');
    const emailInput = document.querySelector('#editEmail');
    const rolesContainer = document.querySelector('#edit-roles-container');
    const passwordInput = document.querySelector('#editPassword');
    idInput.value = user.id;
    nameInput.value = user.username;
    emailInput.value = user.email;
    // Очистите контейнер ролей перед добавлением новых чекбоксов
    rolesContainer.innerHTML = '';

    // Создайте чекбоксы для каждой роли
    try {
        const allRoles = await fetchRoles();
        allRoles.forEach(role => {
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.name = 'roles';
            checkbox.value = role.id;
            checkbox.required = role.authority === 'USER';
            checkbox.checked = user.roles.some(userRole => userRole.id === role.id);

            const label = document.createElement('label');
            label.textContent = role.authority;
            label.prepend(checkbox);

            const div = document.createElement('div');
            div.classList.add('checkbox');
            div.appendChild(label);

            rolesContainer.appendChild(div);
        });
    } catch (error) {
        console.error('Error fetching roles:', error);
    }

    modal.show();

    const okButton = document.querySelector('#editOkButton');
    okButton.addEventListener('click', async () => {
        const updatedUser = {
            id: idInput.value,
            username: nameInput.value,
            email: emailInput.value,
            password: passwordInput.value, // добавляем поле пароля
            roles: Array.from(rolesContainer.querySelectorAll('input[name="roles"]:checked')).map(checkbox => checkbox.value)
        };
        console.log(updatedUser);

        const url = `http://localhost:8080/admin`;
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content'),
                "X-CSRF-HEADER": document.querySelector('meta[name="_csrf_header"]').getAttribute('content')
            },
            body: JSON.stringify(updatedUser)
        });

        if (response.ok) {
            passwordInput.value = '';
            await fetchUsers();
            modal.hide();
        } else {
            alert('Edit was unsuccessful');
            modal.hide();
        }
    });
    const editCloseButton = document.querySelector('#editCloseButton');
    editCloseButton.addEventListener('click', async () => {
        modal.hide();
    })
}



let deleteHandlerAdded = false;
let currentUser = null;
let modal = null;

async function deleteUser(user) {
    modal = new bootstrap.Modal(document.querySelector('#deleteModal'));
    const idInput = document.querySelector('#deleteId');
    idInput.setAttribute('readonly', 'true');
    const nameInput = document.querySelector('#deleteName');
    const emailInput = document.querySelector('#deleteEmail');
    const rolesInput = document.querySelector('#deleteRoles');

    idInput.value = user.id;
    nameInput.value = user.username;
    emailInput.value = user.email;
    rolesInput.value = user.roles.map(role => role.authority).join(', ');

    modal.show();

    currentUser = user;

    const okButton = document.querySelector('#deleteOkButton');
    if (!deleteHandlerAdded) {
        okButton.addEventListener('click', deleteHandler);
        deleteHandlerAdded = true;
    }

    const deleteCloseButton = document.querySelector('#deleteCloseButton');
    deleteCloseButton.addEventListener('click', async () => {
        modal.hide();
    });
}

async function deleteHandler() {
    const response = await fetch(`http://localhost:8080/admin/${currentUser.id}/delete`, {
        method: 'DELETE',
        headers: {
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content'),
            "X-CSRF-HEADER": document.querySelector('meta[name="_csrf_header"]').getAttribute('content')
        }
    });

    if (response.ok) {
        const tableBody = document.querySelector('#usersList tbody');
        const row = document.querySelector(`#usersList tbody tr[data-id="${currentUser.id}"]`);
        tableBody.removeChild(row);
        modal.hide();
        await fetchUsers(); // обновляем список пользователей
    } else {
        alert('Deleting was unsuccessful');
        modal.hide();
    }
}




async function fetchUsers() {
    const url = 'http://localhost:8080/admin/list';
    const response = await fetch(url);
    const users = await response.json();
    console.log(users)

    const tableBody = document.querySelector('#usersList tbody');
    tableBody.innerHTML = '';

    users.forEach(user => {
        const row = document.createElement('tr');
        row.setAttribute('data-id', user.id);

        const idCell = document.createElement('td');
        idCell.textContent = user.id;
        row.appendChild(idCell);

        const nameCell = document.createElement('td');
        nameCell.textContent = user.username;
        row.appendChild(nameCell);

        const emailCell = document.createElement('td');
        emailCell.textContent = user.email;
        row.appendChild(emailCell);

        const rolesCell = document.createElement('td');
        const roles = user.roles.map(role => role.authority).join(', ');
        rolesCell.textContent = roles;
        row.appendChild(rolesCell);

        const editCell = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.setAttribute('class', 'btn btn-primary')
        editButton.textContent = 'Edit';
        editButton.addEventListener('click', () => {
            editUser(user);
        });
        editCell.appendChild(editButton);
        row.appendChild(editCell);

        const deleteCell = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.setAttribute('class', 'btn btn-danger');
        deleteButton.textContent = 'Delete';
        deleteButton.addEventListener('click', () => {
            deleteUser(user);
        });
        deleteCell.appendChild(deleteButton);
        row.appendChild(deleteCell);

        tableBody.appendChild(row);
    });
}

async function fillLoggedUserPage() {
    // Получить пользователя на странице юзера
    fetch('/userData')
        .then(response => response.json())
        .then(data => {
            // Добавить электронную почту пользователя в заголовок страницы
            const userEmail = document.querySelector('#userEmail');
            userEmail.textContent = data.email;

            // Добавить роли пользователя в заголовок страницы
            const userRoles = document.querySelector('#userRoles');
            const roles = data.roles.map(role => role.authority).join(', ');
            userRoles.textContent = roles;

            // Создать новую строку в таблице
            const row = document.createElement('tr');

            // Заполнить новую строку данными пользователя
            const idCell = document.createElement('td');
            idCell.textContent = data.id;
            row.appendChild(idCell);

            const nameCell = document.createElement('td');
            nameCell.textContent = data.username;
            row.appendChild(nameCell);

            const emailCell = document.createElement('td');
            emailCell.textContent = data.email;
            row.appendChild(emailCell);

            const rolesCell = document.createElement('td');
            rolesCell.textContent = roles;
            row.appendChild(rolesCell);

            // Добавить новую строку в таблицу
            const tableBody = document.querySelector('#userPanel tbody');
            tableBody.appendChild(row);
        });
}

async function loadRolesAndSubmitForm() {
    // Получаем ролей и добавляем их на страницу
    fetch("/admin/roles")
        .then(response => response.json())
        .then(data => {
            let rolesContainer = document.querySelector("#roles-container");
            data.forEach(role => {
                let checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.name = "roles";
                checkbox.value = role.id;
                checkbox.required = role.authority === "USER";
                let label = document.createElement("label");
                label.textContent = role.authority;
                label.prepend(checkbox);
                let div = document.createElement("div");
                div.classList.add("checkbox");
                div.appendChild(label);
                rolesContainer.appendChild(div);
            });
        })
        .catch(error => console.error(error));

    // Отправляем данные формы при ее отправке
    let userForm = document.querySelector("#user-form");
    userForm.addEventListener("submit", function(event) {
        event.preventDefault();
        let userData = {
            id: -1,
            username: document.querySelector("#name").value,
            password: document.querySelector("#password").value,
            email: document.querySelector("#email").value,
            roles: []
        };
        let checkboxes = document.querySelectorAll('input[name="roles"]:checked');
        for (let i = 0; i < checkboxes.length; i++) {
            userData.roles.push(checkboxes[i].value);
        }
        console.log(userData);
        fetch("/admin", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content'),
                "X-CSRF-HEADER": document.querySelector('meta[name="_csrf_header"]').getAttribute('content')
            },
            body: JSON.stringify(userData)
        })
            .then(response => {
                if (response.ok) {
                    alert("User created successfully!");
                    fetchUsers();
                } else {
                    alert("Error creating user.");
                }
            })
            .catch(error => console.error(error));
    });
}










