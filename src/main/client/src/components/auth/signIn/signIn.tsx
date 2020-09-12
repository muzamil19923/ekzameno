import { Formik } from "formik";
import { FormikControl } from "formik-react-bootstrap";
import React from "react";
import { Button, Form, Modal, Tab } from "react-bootstrap";
import { useDispatch, useSelector } from "react-redux";
import * as yup from "yup";
import { selectMe, signIn } from "../../../redux/slices/usersSlice";

interface Props {
  onHide: () => any,
}

interface FormValues {
  email: string,
  password: string,
}

const FormSchema = yup.object().shape({
  email: yup.string().email().required(),
  password: yup.string().required(),
});

export const SignIn = (props: Props): JSX.Element => {
  const dispatch = useDispatch();
  const me = useSelector(selectMe);

  const onSubmit = (values: FormValues): void => {
    dispatch(signIn(values));
  };

  if (me !== undefined) {
    props.onHide();
    return <></>;
  }

  return (
    <Tab.Pane eventKey="signIn">
      <Formik
        initialValues={{ email: "", password: "" }}
        onSubmit={onSubmit}
        validationSchema={FormSchema}
      >
        {
          ({
            handleSubmit,
            isSubmitting,
          }) => (
            <Form
              id="signIn"
              onSubmit={handleSubmit as any}>
              <Modal.Body>
                <FormikControl
                  label="Email"
                  name="email"
                  placeholder="Enter email" />
                <FormikControl
                  label="Password"
                  name="password"
                  type="password"
                  placeholder="Enter password" />
              </Modal.Body>
              <Modal.Footer>
                <Button type="submit" variant="success" disabled={isSubmitting}>
                  Sign In
                </Button>
              </Modal.Footer>
            </Form>
          )
        }
      </Formik>
    </Tab.Pane>
  );
};
