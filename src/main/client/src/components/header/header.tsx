import React, { useState } from "react";
import { Button, Container, Form, Navbar } from "react-bootstrap";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { selectMe, signOut } from "../../redux/slices/usersSlice";
import { AuthModal } from "../auth/authModal";
import { CreateSubjectModal } from "../subject/subjectModal";

export const Header = (): JSX.Element => {
  const [authModalShow, setAuthModalShow] = useState(false);
  const [createSubjectshow, setcreateSubjectShow] = useState(false);
  const dispatch = useDispatch();
  const me = useSelector(selectMe);

  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Link className="navbar-brand" to="/">Ekzameno</Link>
        <Button onClick={() => setcreateSubjectShow(true)}>
          Create Subject
        </Button>
        <CreateSubjectModal show={createSubjectshow} onHide={() => setcreateSubjectShow(false)} />
        <Form className="ml-auto" inline>
          {
            me === undefined ?
              <Button
                variant="outline-info"
                onClick={() => setAuthModalShow(true)}>
                Sign In
              </Button>
              :
              <Button
                variant="outline-info"
                onClick={() => dispatch(signOut())}>
                Sign Out
              </Button>
          }
        </Form>
        <AuthModal show={authModalShow} onHide={() => setAuthModalShow(false)} />
      </Container>
    </Navbar>
  );
};
