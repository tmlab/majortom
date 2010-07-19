CREATE TRIGGER trigger_detect_duplicate_names AFTER INSERT ON names FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_names();
CREATE TRIGGER trigger_detect_duplicate_names_on_update BEFORE UPDATE ON names FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_names();
CREATE TRIGGER trigger_detect_duplicate_occurrences BEFORE INSERT OR UPDATE ON occurrences FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_occurrences();
CREATE TRIGGER trigger_detect_duplicate_variants BEFORE INSERT OR UPDATE ON variants FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_variants();
CREATE TRIGGER trigger_detect_duplicate_associations AFTER UPDATE ON roles FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_associations();
CREATE TRIGGER trigger_detect_duplicate_roles BEFORE INSERT OR UPDATE ON roles FOR EACH ROW EXECUTE PROCEDURE detect_duplicate_roles();
CREATE TRIGGER trigger_check_reification_condition BEFORE INSERT OR UPDATE ON reifiables FOR EACH ROW EXECUTE PROCEDURE check_reification_condition();