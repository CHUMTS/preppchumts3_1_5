$(async function () {
    await fillLoggedUserPage();
})


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