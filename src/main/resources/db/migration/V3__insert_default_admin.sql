INSERT INTO admins (
    role_id,
    mail,
    password,
    employee_code,
    creation_date,
    modification_date
)
SELECT
    role.id,
    'admin@admin.admin',
    'adminadmin',
    'ADMIN-001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM roles role
WHERE role.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM admins admin
      WHERE LOWER(admin.mail) = LOWER('admin@admin.admin')
  )
ORDER BY role.id
LIMIT 1;
