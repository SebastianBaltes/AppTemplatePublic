function confirmDelete(question) {
//	return confirm('Möchten Sie den Datensatz wirklich Löschen?');
	if (typeof question === 'undefined')
		question = 'Möchten Sie den Datensatz wirklich Löschen?';
	return confirm(question);
}
