INSERT INTO roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'ADMIN'
);

INSERT INTO roles (name)
SELECT 'AGENT'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'AGENT'
);
