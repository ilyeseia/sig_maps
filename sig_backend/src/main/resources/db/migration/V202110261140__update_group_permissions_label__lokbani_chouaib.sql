
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 26 October, 2021  à 11:40
--------
--------scripts ::  Fix typo in group permission label
--------
--

--

UPDATE sig.permissions
SET label='Afficher un groupe'
WHERE id = 'ba83b693-f26a-4648-9443-3bd80744263c';

UPDATE sig.permissions
SET label='Supprimer un groupe'
WHERE id = '4756ef43-2488-4d05-b87e-c3ab80aadf39';
